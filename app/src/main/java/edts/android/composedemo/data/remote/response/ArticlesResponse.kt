package edts.android.composedemo.data.remote.response

import com.google.gson.annotations.SerializedName
import edts.android.composedemo.data.remote.response.ArticleItemResponse

data class ArticlesResponse(
    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("totalResults")
    val totalResults: Int? = null,

    @field:SerializedName("page")
    val page: Int? = null,

    @field:SerializedName("pageSize")
    val pageSize: Int? = null,

    @field:SerializedName("articles")
    val articles: List<ArticleItemResponse>? = null
)
