package edts.android.composedemo.ui.screen.api_demo

import edts.android.composedemo.domain.model.ArticleItemData

data class ApiDemoScreenState(
    val isLoading: Boolean = false,
    val articles: List<ArticleItemData> = emptyList(),
    val isApiKeyExist: Boolean = false
)
