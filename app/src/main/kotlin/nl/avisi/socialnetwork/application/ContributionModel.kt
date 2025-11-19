package nl.avisi.socialnetwork.application

import nl.avisi.socialnetwork.domain.contribution.Contribution
import nl.avisi.socialnetwork.domain.contribution.Post
import java.time.OffsetDateTime
import java.util.UUID

data class PostModel(
    val id: UUID,
    val author: String,
    val time: OffsetDateTime,
    val content: RichTextModel,
    val likes: Set<String>
) {
    companion object {
        fun of(post: Post): PostModel {
            return PostModel(
                post.id.asUUID(),
                post.author.toString(),
                post.creationTime,
                RichTextModel.of(post.content),
                post.likes.map { it.toString() }.toSet()
            )
        }
    }
}

data class ContributionModel(
    val id: UUID,
    val openingPost: PostModel,
    val replies: List<PostModel> = emptyList()
) {
    companion object {
        fun of(contribution: Contribution) =
            ContributionModel(
                contribution.id.asUUID(),
                PostModel.of(contribution.openingPost),
                contribution.replies.map { PostModel.of(it) }
            )
    }
}
