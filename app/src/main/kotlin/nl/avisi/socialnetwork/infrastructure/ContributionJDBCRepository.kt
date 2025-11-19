package nl.avisi.socialnetwork.infrastructure

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import nl.avisi.socialnetwork.domain.contribution.*
import nl.avisi.socialnetwork.domain.richtext.*
import nl.avisi.socialnetwork.domain.user.Username
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class ContributionJDBCRepository(
    private val jdbcTemplate: JdbcTemplate
) : ContributionRepository {
    private val module = SerializersModule {
        polymorphic(TextSpan::class) {
            subclass(StyledText::class)
            subclass(Link::class)
            subclass(Mention::class)
            subclass(Tag::class)
        }
    }
    private val json = Json {
        serializersModule = module
    }

    override fun nextContributionID(): ContributionID =
        ContributionID(UUID.randomUUID())

    override fun nextPostID(): PostID =
        PostID(UUID.randomUUID())

    override fun insert(contribution: Contribution) {
        jdbcTemplate.update("insert into contribution (id) values (?)", contribution.id.asUUID())

        insertPost(contribution.id, contribution.openingPost, 0)

        contribution.replies.forEachIndexed { index, post ->
            insertPost(contribution.id, post, index + 1)
        }
    }

    override fun update(contribution: Contribution) {
        updatePost(contribution.openingPost)

        contribution.replies.forEachIndexed { index, post ->
            if (postExists(post.id))
                updatePost(post)
            else
                insertPost(contribution.id, post, index + 1)
        }
    }

    private fun insertPost(contributionID: ContributionID, post: Post, ordering: Int) {
        jdbcTemplate.update(
            "INSERT INTO post (id, contribution_id, ordering, author, time, contents) VALUES (?, ?, ?, ?, ?, ?)",
            post.id.asUUID(),
            contributionID.asUUID(),
            ordering,
            post.author.toString(),
            post.creationTime,
            json.encodeToString(post.content)
        )
    }

    private fun updatePost(post: Post) {
        jdbcTemplate.update(
            "update post set contents = ? where id = ?",
            json.encodeToString(post.content),
            post.id
        )
    }

    private fun postExists(postID: PostID): Boolean =
        (jdbcTemplate.queryForObject(
            "select count(*) from post where id = ?",
            Int::class.java,
            postID.asUUID()
        ) ?: 0) > 0

    override fun findByID(contributionID: ContributionID): Contribution? {
        val contribution = jdbcTemplate.query<Contribution>(
            "select * from post where contribution_id = ? and ordering = 0",
            { rs, _ ->
                Contribution.newContribution(
                    contributionID,
                    PostID(rs.getObject("id", UUID::class.java)),
                    Username(rs.getString("author")),
                    rs.getObject("time", OffsetDateTime::class.java),
                    json.decodeFromString(rs.getString("contents"))
                )
            },
            contributionID.asUUID()
        ).singleOrNull() ?: return null

        jdbcTemplate.query(
            "select * from post where contribution_id = ? and ordering > 0",
            { rs ->
                contribution.newReply(
                    PostID(rs.getObject("id", UUID::class.java)),
                    Username(rs.getString("author")),
                    rs.getObject("time", OffsetDateTime::class.java),
                    json.decodeFromString(rs.getString("contents"))
                )
            },
            contribution.id.asUUID()
        )

        return contribution
    }
}
