package edts.android.composedemo.ui.screen.overlay

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import edts.android.composedemo.ui.theme.ColorSemitransparent
import edts.android.composedemo.ui.theme.ComposeDemoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun OverlayServiceScreen(
    modifier: Modifier = Modifier,
    delegate: OverlayServiceDelegate
) {
    var sheetVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    fun doClose(){
        scope.launch {
            sheetVisible = false
            delay(100)
            delegate.doStopSelf()
        }
    }

    // to make initial visibility shown with animation
    LaunchedEffect(Unit) {
        delay(100)
        sheetVisible = true
    }

    ComposeDemoTheme {
        Box(
            modifier = modifier.fillMaxSize()
        ) {
            // Dimmed background
            if (sheetVisible) {
                Box(
                    Modifier
                        .fillMaxSize()
                        .background(ColorSemitransparent) // semi-transparent black
                        .clickable { doClose() }
                )
            }

            // Bottom Sheet Content
            AnimatedVisibility(
                visible = sheetVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it }),
                modifier = Modifier.align(Alignment.BottomCenter)
            ) {
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 200.dp, max = 400.dp)
                        // Add padding for navigation bar so content doesn't get hidden
                        .windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "📦 Custom Bottom Sheet",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(12.dp))
                        Text("This layout dims the background without covering other apps.")
                        Spacer(Modifier.height(24.dp))
                        Button(onClick = {
                            doClose()
                        }) {
                            Text("Close Overlay")
                        }
                    }
                }
            }
        }
    }
}