package edts.android.composedemo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.constants.homeDestinations
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.ui.component.HomeButtonComp
import edts.android.composedemo.ui.modifier.conditional
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateTo: (Destinations) -> Unit = {}
) {
    val context = LocalContext.current
    DemoScaffoldComp(
        modifier = modifier,
        title = "Compose Demo",
        isLargeTopAppBar = true
    ) {innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            itemsIndexed(homeDestinations){index, it->
                HomeButtonComp(
                    text = it.title,
                    modifier = Modifier.conditional(
                        condition = index == 0,
                        ifTrue = { padding(top = 8.dp) }
                    )
                ) {
                    try {
                        navigateTo(it)
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Destination Not Ready Yet",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}

//@Preview(name = "Main")
//@PreviewFontScale
//@PreviewLightDark
//@PreviewScreenSizes
@Preview
@Composable
private fun HomePreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = { HomeScreen() }
    )
}

@Preview
@Composable
private fun HomeSinglePreview() {
    HomeScreen()
}