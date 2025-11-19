package nl.avisi.socialnetwork.domain.richtext

import kotlinx.serialization.Serializable
import nl.avisi.socialnetwork.domain.user.Username

@Serializable
sealed class TextSpan

@Serializable
data class StyledText(val text: String, val bold: Boolean = false, val italic: Boolean = false) : TextSpan() {
    init {
        require(text.isNotBlank())
    }
}

@Serializable
data class Link(val text: StyledText, val url: String) : TextSpan()

@Serializable
data class Mention(val username: Username) : TextSpan()

@Serializable
data class Tag(val name: String) : TextSpan()
