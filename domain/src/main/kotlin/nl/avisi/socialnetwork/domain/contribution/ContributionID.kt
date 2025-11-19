package nl.avisi.socialnetwork.domain.contribution

import java.util.UUID

@JvmInline
value class ContributionID(private val id: UUID) {
    fun asUUID() = id
}
