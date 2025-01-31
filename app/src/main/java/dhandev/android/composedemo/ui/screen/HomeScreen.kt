package dhandev.android.composedemo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.ThemeMode
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.HomeButtonComp
import dhandev.android.composedemo.utils.preview.PreviewWrapperComp
import dhandev.android.composedemo.utils.preview.ThemePreviewProvider

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navigateTo: (Destinations) -> Unit = {}
) {
    val destinations = listOf(
        Destinations.SimpleComponent(),
        Destinations.ComposeModifier(),
        Destinations.StateManagement(),
        Destinations.SideEffect(),
        Destinations.Theming(),
        Destinations.Compatibility(),
    )
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
            items(destinations){
                HomeButtonComp(text = it.title) {
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