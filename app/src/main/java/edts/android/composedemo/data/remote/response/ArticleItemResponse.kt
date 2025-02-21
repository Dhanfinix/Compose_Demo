package edts.android.composedemo.data.remote.response

import com.google.gson.annotations.SerializedName

data class ArticleItemResponse(
    @field:SerializedName("link")
    val link: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("publishDate")
    val publishDate: String? = null,

    @field:SerializedName("authors")
    val authors: String? = null,

    @field:SerializedName("source")
    val source: String? = null,

    @field:SerializedName("language")
    val language: String? = null,

    @field:SerializedName("sentiment")
    val sentiment: String? = null,

    @field:SerializedName("confidence")
    val confidence: Double? = null,

    @field:SerializedName("content")
    val content: String? = null
)