package dhandev.android.composedemo.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dhandev.android.composedemo.ui.component.DemoScaffoldComp

@Composable
fun SimpleComponentScreen(modifier: Modifier = Modifier) {
    DemoScaffoldComp(
        modifier = modifier,
        title = "Simple Component",
        isLargeTopAppBar = false
    ) {
        Box(Modifier.fillMaxSize()) {
            Text(
                "Simple Component",
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}