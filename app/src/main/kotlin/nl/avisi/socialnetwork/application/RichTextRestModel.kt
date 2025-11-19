package nl.avisi.socialnetwork.application

import nl.avisi.socialnetwork.domain.richtext.Link
import nl.avisi.socialnetwork.domain.richtext.Mention
import nl.avisi.socialnetwork.domain.richtext.RichText
import nl.avisi.socialnetwork.domain.richtext.StyledText
import nl.avisi.socialnetwork.domain.richtext.Tag
import nl.avisi.socialnetwork.domain.richtext.TextSpan
import nl.avisi.socialnetwork.domain.user.Username

enum class TextSpanType {
    STYLED_TEXT, LINK, MENTION, TAG
}

data class TextSpanModel(
    val type: TextSpanType,
    val text: String?,
    val bold: Boolean?,
    val italic: Boolean?,
    val url: String?,
    val username: String?,
    val tagName: String?
)

data class RichTextModel(
    val spans: List<TextSpanModel>,
)

fun RichTextModel.asRichText(): RichText =
    RichText(spans.map { it.asTextSpan() })

fun TextSpanModel.asTextSpan(): TextSpan =
    when (type) {
        TextSpanType.STYLED_TEXT -> StyledText(text ?: "", bold ?: false, italic ?: false)
        TextSpanType.LINK -> Link(StyledText(text ?: ""), url!!)
        TextSpanType.MENTION -> Mention(Username(username ?: ""))
        TextSpanType.TAG -> Tag(tagName ?: "")
    }
