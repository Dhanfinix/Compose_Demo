package edts.android.composedemo.domain.model

data class ArticlesData(
    val status: String? = null,
    val totalResults: Int? = null,
    val page: Int? = null,
    val pageSize: Int? = null,
    val articles: List<ArticleItemData>? = null
)
