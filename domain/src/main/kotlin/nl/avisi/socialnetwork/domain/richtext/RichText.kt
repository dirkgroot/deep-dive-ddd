package nl.avisi.socialnetwork.domain.richtext

import kotlinx.serialization.Serializable

@Serializable
data class RichText(
    val spans: List<TextSpan>
) {
    init {
        require(spans.isNotEmpty())
    }

    val links get() = spans.filterIsInstance<Link>().map { it.url }
    val mentions get() = spans.filterIsInstance<Mention>().map { it.username }
    val tags get() = spans.filterIsInstance<Tag>().map { it.name }
}
