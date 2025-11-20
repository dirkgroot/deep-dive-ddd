package nl.avisi.socialnetwork.infrastructure

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import nl.avisi.socialnetwork.domain.contribution.*
import nl.avisi.socialnetwork.domain.user.Username
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.time.OffsetDateTime
import java.util.*

@Repository
class ContributionJDBCRepository(
    private val jdbcTemplate: JdbcTemplate
) : ContributionRepository {
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
            Json.encodeToString(post.content)
        )

        insertLikes(post)
    }

    private fun updatePost(post: Post) {
        jdbcTemplate.update(
            "update post set contents = ? where id = ?",
            Json.encodeToString(post.content),
            post.id.asUUID()
        )
        jdbcTemplate.update("delete from post_liked_by where post_id = ?", post.id.asUUID())
        insertLikes(post)
    }

    private fun insertLikes(post: Post) {
        post.likes.forEach {
            jdbcTemplate.update(
                "INSERT INTO post_liked_by (post_id, username) VALUES (?, ?)",
                post.id.asUUID(),
                it.toString()
            )
        }
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
                    Json.decodeFromString(rs.getString("contents"))
                )
            },
            contributionID.asUUID()
        ).singleOrNull() ?: return null

        jdbcTemplate.query(
            "select * from post_liked_by where post_id = ?",
            { rs, _ -> contribution.likedBy(Username(rs.getString("username"))) },
            contribution.openingPost.id.asUUID()
        )

        jdbcTemplate.query(
            "select * from post where contribution_id = ? and ordering > 0 order by ordering",
            { rs ->
                val reply = contribution.newReply(
                    PostID(rs.getObject("id", UUID::class.java)),
                    Username(rs.getString("author")),
                    rs.getObject("time", OffsetDateTime::class.java),
                    Json.decodeFromString(rs.getString("contents"))
                )
                jdbcTemplate.query(
                    "select * from post_liked_by where post_id = ?",
                    { rs, _ -> contribution.replyLikedBy(reply.id, Username(rs.getString("username"))) },
                    reply.id.asUUID()
                )
            },
            contribution.id.asUUID()
        )

        return contribution
    }
}
