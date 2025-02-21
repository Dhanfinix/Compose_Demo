package edts.android.composedemo.data.mapper

import edts.android.composedemo.data.remote.response.ArticleItemResponse
import edts.android.composedemo.data.remote.response.ArticlesResponse
import edts.android.composedemo.domain.model.ArticleItemData
import edts.android.composedemo.domain.model.ArticlesData

fun ArticlesResponse.toModel() = ArticlesData(
    status = status,
    totalResults = totalResults,
    page = page,
    pageSize = pageSize,
    articles = articles?.map { it.toModel() }
)

fun ArticleItemResponse.toModel() = ArticleItemData(
    link = link,
    title = title,
    publishDate = publishDate,
    authors = authors,
    source = source,
    language = language,
    sentiment = sentiment,
    confidence = confidence,
    content = content
)

