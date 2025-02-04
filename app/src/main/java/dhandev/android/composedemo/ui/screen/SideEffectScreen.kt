package dhandev.android.composedemo.ui.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.ThemeMode
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.OnlineStatus
import dhandev.android.composedemo.ui.component.side_effect.NetworkStatusObserver
import dhandev.android.composedemo.utils.preview.PreviewWrapperComp
import dhandev.android.composedemo.utils.preview.ThemePreviewProvider
import kotlinx.coroutines.delay

@Composable
fun SideEffectScreen(
    modifier: Modifier = Modifier
) {
    var showAutoLaunchedDialog by remember { mutableStateOf(false) }
    var recompositionCount by remember { mutableIntStateOf(0) }
    var isOnline by remember { mutableStateOf(false) }
    /** used to prevent LaunchedEffect isOnline is triggered at initialization */
    var onlineChangedByObserver by remember { mutableStateOf(false) }
    val context = LocalContext.current
    /** An effect that start after composition */
    LaunchedEffect(Unit) {
        delay(1500)
        showAutoLaunchedDialog = true
    }
    /** An effect that start if there's change on the isOnline key */
    LaunchedEffect(isOnline) {
        if (onlineChangedByObserver){
            Toast.makeText(
                context,
                "You're ${if (isOnline) "Online" else "Offline"}",
                Toast.LENGTH_SHORT)
            .show()
        }
        onlineChangedByObserver = true
    }

    NetworkStatusObserver(
        onNetworkAvailable = {isOnline = it}
    )

    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.SideEffect().title,
    ) {
        /** An effect that triggered everytime demoScaffoldComp is recomposed */
        SideEffect {
            recompositionCount++
            Log.d("isOnline State", "$isOnline")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("There is 3 side-effect in this demo i want to share:\n" +
                        "1. LaunchedEffect\n" +
                        "2. DisposableEffect\n" +
                        "3. SideEffect",
                )
            }
            item {
                Text("Recomposition count by SideEffect: $recompositionCount")
            }
            item {
                OnlineStatus(isOnline)
            }
        }

        AnimatedVisibility(showAutoLaunchedDialog) {
            AlertDialog(
                onDismissRequest = {showAutoLaunchedDialog = false},
                confirmButton = {
                    TextButton(
                        onClick = { showAutoLaunchedDialog = false }
                    ) {
                        Text("Close")
                    }
                },
                title = {
                    Text("This dialog automatically appear because of launched effect, " +
                            "an effect that triggered by it's key, in this case is Unit " +
                            "(this screen existence)")
                }
            )
        }
    }
}

@Preview
@Composable
private fun SideEffectPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = { SideEffectScreen() }
    )
}