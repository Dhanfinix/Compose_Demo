package dhandev.android.composedemo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.ui.component.DemoScaffoldComp

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
        title = "Modifier"
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Example 1: Background first, then padding
            // This shows how the order of modifiers affects the layout.
            Box(
                modifier = Modifier
                    .background(Color.Red)  // Background applied first
                    .padding(16.dp)         // Padding applied inside the background
                    .size(100.dp)
            ) {
                Text("Background first", color = Color.White)
            }

            // Example 2: Padding first, then background
            // This demonstrates how applying padding before background changes the visual appearance.
            Box(
                modifier = Modifier
                    .padding(16.dp)         // Padding applied first
                    .background(Color.Green) // Background applied outside the padding
                    .size(100.dp)
            ) {
                Text("Padding first", color = Color.White)
            }

            // Example 3: Rounded corner background
            // Shows how to apply a rounded corner shape to the background.
            Box(
                modifier = Modifier
                    .background(Color.Blue, RoundedCornerShape(16.dp))  // Rounded corner background
                    .padding(16.dp)
                    .size(100.dp)
            ) {
                Text("Rounded corner", color = Color.White)
            }

            // Example 4: Clickable box with clipped shape
            // Ensures the ripple effect respects the rounded corners by using the clip modifier.
            Box(
                modifier = Modifier
                    .background(Color.Blue, CutCornerShape(100))
                    .clip(CutCornerShape(100))  // Ensures the content, including the ripple effect, is clipped to the specified shape.
                    .clickable {
                        Toast.makeText(context, "Clickable Compose Modifier", Toast.LENGTH_SHORT).show()
                    }
                    .padding(16.dp)
                    .size(100.dp)
            ) {
                Text(
                    text = "Clickable",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
