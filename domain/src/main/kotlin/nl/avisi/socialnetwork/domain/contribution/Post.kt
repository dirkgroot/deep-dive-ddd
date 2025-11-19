package nl.avisi.socialnetwork.domain.contribution

import nl.avisi.socialnetwork.domain.richtext.RichText
import nl.avisi.socialnetwork.domain.user.Username
import java.time.OffsetDateTime
import java.util.*

class Post private constructor(
    val id: PostID,
    val creationTime: OffsetDateTime,
    val author: Username,
    content: RichText
) {
    companion object {
        internal fun newPost(postID: PostID, author: Username, time: OffsetDateTime, content: RichText): Post =
            Post(postID, time, author, content)
    }

    private val likedBy: MutableSet<Username> = mutableSetOf()

    var content: RichText = content
        private set

    val likes: Set<Username> get() = likedBy.toSet()

    internal fun likedBy(username: Username) {
        likedBy.add(username)
    }

    internal fun edit(newContent: RichText) {
        require(isLessThanOneDayOld) { "Post cannot be updated when it's more than one day old" }
        this.content = newContent
    }

    private val isLessThanOneDayOld: Boolean
        get() = OffsetDateTime.now().minusDays(1).isBefore(creationTime)

    override fun equals(other: Any?): Boolean =
        when (other) {
            null -> false
            is Post -> other.id == this.id
            else -> false
        }

    override fun hashCode(): Int = Objects.hash(id)
}
