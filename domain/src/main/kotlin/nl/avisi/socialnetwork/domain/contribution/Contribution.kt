package nl.avisi.socialnetwork.domain.contribution

import nl.avisi.socialnetwork.domain.richtext.RichText
import nl.avisi.socialnetwork.domain.user.Username
import java.time.OffsetDateTime
import java.util.*

class Contribution private constructor(val id: ContributionID, openingPost: Post) {
    private val posts: MutableList<Post> = mutableListOf(openingPost)

    companion object {
        fun newContribution(
            id: ContributionID,
            openingPostID: PostID,
            author: Username,
            time: OffsetDateTime,
            postContent: RichText
        ): Contribution =
            Contribution(id, Post.newPost(openingPostID, author, time, postContent))
    }

    val openingPost: Post get() = posts.first()
    val replies: List<Post> get() = posts.drop(1).toList()

    fun newReply(postID: PostID, author: Username, time: OffsetDateTime, postContent: RichText): Post {
        val reply = Post.newPost(postID, author, time, postContent)
        posts.add(reply)
        return reply
    }

    fun editOpeningPost(newContent: RichText) {
        openingPost.edit(newContent)
    }

    fun editReply(postID: PostID, newContent: RichText) {
        require(replies.any { it.id == postID }) { "Post is not part of this contribution" }

        replies.first { it.id == postID }
            .apply { edit(newContent) }
    }

    fun likedBy(username: Username) {
        openingPost.likedBy(username)
    }

    fun replyLikedBy(postID: PostID, username: Username) {
        require(replies.any { it.id == postID }) { "Post is not part of this contribution" }

        replies.first { it.id == postID }
            .apply { likedBy(username) }
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            null -> false
            is Contribution -> other.id == this.id
            else -> false
        }

    override fun hashCode(): Int = Objects.hash(id)
}
