package edts.android.composedemo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

/**
 * Demonstrates various Modifier usage in Jetpack Compose.
 *
 * @param modifier Modifier to be applied to the Scaffold.
 */
@Composable
fun ComposeModifierScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.ComposeModifier().title
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text("Modifier are all needed for these")
            }

            // Example 1: Background first, then padding
            // This shows how the order of modifiers affects the layout.
            item {
                Box(
                    modifier = Modifier
                        .background(Color.Red)  // Background applied first
                        .padding(16.dp)         // Padding applied inside the background
                        .size(100.dp)
                        .semantics {
                            contentDescription = "A Background First Modifier"
                        }
                ) {
                    Text("Background first", color = Color.White)
                }
            }

            // Example 2: Padding first, then background
            // This demonstrates how applying padding before background changes the visual appearance.
            item {
                Box(
                    modifier = Modifier
                        .padding(16.dp)         // Padding applied first
                        .background(Color.Green) // Background applied outside the padding
                        .size(100.dp)
                ) {
                    Text("Padding first", color = Color.White)
                }
            }

            // Example 3: Rounded corner background
            // Shows how to apply a rounded corner shape to the background.
            item {
                Box(
                    modifier = Modifier
                        .shadow(20.dp, RoundedCornerShape(16.dp))
                        .background(Color.Blue, RoundedCornerShape(16.dp))  // Rounded corner background
                        .padding(16.dp)
                        .size(100.dp)
                ) {
                    Text("Rounded corner & Shadow", color = Color.White)
                }
            }

            // Example 4: Clickable box with clipped shape
            // Ensures the ripple effect respects the rounded corners by using the clip modifier.
            item {
                Box(
                    modifier = Modifier
                        .background(Color.Yellow, CutCornerShape(100))
                        .clip(CutCornerShape(100))  // Ensures the content, including the ripple effect, is clipped to the specified shape.
                        .clickable {
                            Toast.makeText(context, "Clickable Compose Modifier", Toast.LENGTH_SHORT).show()
                        }
                        .padding(16.dp)
                        .size(100.dp)
                ) {
                    Text(
                        text = "Clickable",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }

            // Example 5: Using graphicsLayer for transformations
            // Demonstrates applying rotation, scale, and alpha transformations.
            item {
                Box(
                    modifier = Modifier
                        .graphicsLayer(
                            rotationZ = 60f, // Rotate 45 degrees
                            translationX = 300f,
                            translationY = -200f,
                            scaleX = 1.2f, // Scale horizontally by 1.2x
                            scaleY = 1.2f, // Scale vertically by 1.2x
                            alpha = 0.8f // Set alpha to 80%
                        )
                        .background(Color.Magenta)
                        .padding(16.dp)
                        .size(100.dp)
                ) {
                    Text(
                        "Graphics Layer",
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ComposeModifierPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = { ComposeModifierScreen() }
    )
}
