package nl.avisi.socialnetwork.domain.contribution

import java.util.UUID

@JvmInline
value class PostID(private val id: UUID) {
    fun asUUID() = id
}
