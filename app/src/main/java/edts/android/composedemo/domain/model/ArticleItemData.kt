package edts.android.composedemo.domain.model


data class ArticleItemData(
    val link: String? = null,
    val title: String? = null,
    val publishDate: String? = null,
    val authors: String? = null,
    val source: String? = null,
    val language: String? = null,
    val sentiment: String? = null,
    val confidence: Double? = null,
    val content: String? = null
)