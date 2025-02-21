package edts.android.composedemo.ui.screen.api_demo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.domain.usecase.DummyDemoInteractor
import edts.android.composedemo.ui.component.ApiKeyMessage
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.ui.component.NewsItemComp
import edts.android.composedemo.ui.component.ShimmerNewsItemComp
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiDemoScreen(
    modifier: Modifier = Modifier,
    viewModel: ApiDemoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.ApiDemo().title
    ) {innerPadding->
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.getArticles(true) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ){
                if (uiState.isApiKeyExist){
                    if (uiState.isLoading){
                        items(5){
                            ShimmerNewsItemComp()
                        }
                    } else {
                        items(uiState.articles) {
                            NewsItemComp(articleItemData = it)
                        }
                    }
                } else {
                    item {
                        ApiKeyMessage()
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun ApiDemoScreenPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode
    ){
        ApiDemoScreen(
            viewModel = ApiDemoViewModel(DummyDemoInteractor())
        )
    }
}