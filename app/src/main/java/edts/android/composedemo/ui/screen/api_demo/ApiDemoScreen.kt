package edts.android.composedemo.ui.screen.api_demo

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.domain.usecase.DummyDemoInteractor
import edts.android.composedemo.ui.component.ApiKeyMessage
import edts.android.composedemo.ui.component.NewsItemComp
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ){
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ){
                if (uiState.isApiKeyExist){
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    viewModel.getArticles(true)
                                }
                            ) {
                                Text("Load Article")
                            }
                        }
                    }
                    items(uiState.articles) {
                        NewsItemComp(articleItemData = it)
                    }
                } else {
                    item {
                        ApiKeyMessage()
                    }
                }
            }
            AnimatedVisibility(
                visible = uiState.isLoading,
                modifier = Modifier.align(Alignment.Center)
            ) {
                CircularProgressIndicator()
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