package dhandev.android.composedemo.ui.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.ui.component.DemoScaffoldComp

@Composable
fun SideEffectScreen(
    modifier: Modifier = Modifier
) {
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.SideEffect().title,
    ) {
        Text("There is 3 side-effect in this demo i want to share:\n" +
                "1. LaunchedEffect\n" +
                "2. DisposableEffect\n" +
                "3. SideEffect",
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp)
        )
        //TODO: Add demo here
    }
}