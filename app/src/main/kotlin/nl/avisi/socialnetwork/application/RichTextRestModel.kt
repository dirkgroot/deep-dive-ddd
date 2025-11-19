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
    val text: String? = null,
    val bold: Boolean? = null,
    val italic: Boolean? = null,
    val url: String? = null,
    val username: String? = null,
    val tagName: String? = null
) {
    companion object {
        fun of(span: TextSpan): TextSpanModel =
            when (span) {
                is StyledText -> TextSpanModel(TextSpanType.STYLED_TEXT, span.text, span.bold, span.italic)
                is Link -> TextSpanModel(TextSpanType.LINK, span.text.text, span.text.bold, span.text.italic, span.url)
                is Mention -> TextSpanModel(TextSpanType.MENTION, username = span.username.toString())
                is Tag -> TextSpanModel(TextSpanType.TAG, tagName = span.name)
            }
    }
}

data class RichTextModel(
    val spans: List<TextSpanModel>,
) {
    companion object {
        fun of(content: RichText): RichTextModel =
            RichTextModel(
                content.spans.map {
                    TextSpanModel.of(it)
                }
            )
    }
}

fun RichTextModel.asRichText(): RichText =
    RichText(spans.map { it.asTextSpan() })

fun TextSpanModel.asTextSpan(): TextSpan =
    when (type) {
        TextSpanType.STYLED_TEXT -> StyledText(text ?: "", bold ?: false, italic ?: false)
        TextSpanType.LINK -> Link(StyledText(text ?: ""), url!!)
        TextSpanType.MENTION -> Mention(Username(username ?: ""))
        TextSpanType.TAG -> Tag(tagName ?: "")
    }
