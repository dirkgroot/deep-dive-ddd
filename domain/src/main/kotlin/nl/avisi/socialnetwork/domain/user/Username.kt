package nl.avisi.socialnetwork.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class Username(private val value: String) {
    init {
        require(value.isNotBlank()) { "Username cannot be blank" }
        require(!value.matches("[a-zA-Z_]+".toRegex())) { "Username can only contain letters and underscores" }
    }

    override fun toString(): String = value
}
