package edts.android.composedemo.ui.modifier

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.IntSize

fun Modifier.shimmer(): Modifier =
    composed {
        val colorNeutral30 = Color(0xFFDCDEE3)
        val colorNeutral20 = Color(0xFFEFF3F6)
        var size by remember { mutableStateOf(IntSize.Zero) }

        if (LocalInspectionMode.current) {
            // Static color for preview mode
            this.onGloballyPositioned { size = it.size }
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        colors = listOf(colorNeutral20, colorNeutral30, colorNeutral20),
                        start = Offset(0f, 0f),
                        end = Offset(size.width.toFloat(), size.height.toFloat())
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush)
                    }
                }
        } else {
            val transition = rememberInfiniteTransition(label = "shimmer")
            val startOffsetX by transition.animateFloat(
                initialValue = -2 * size.width.toFloat(),
                targetValue = 2 * size.width.toFloat(),
                animationSpec = infiniteRepeatable(animation = tween(1500)),
                label = "shimmer",
            )

            this.onGloballyPositioned { size = it.size }
                .drawWithCache {
                    val brush = Brush.linearGradient(
                        colors = listOf(colorNeutral20, colorNeutral30, colorNeutral20),
                        start = Offset(startOffsetX, 0f),
                        end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
                    )
                    onDrawWithContent {
                        drawContent()
                        drawRect(brush)
                    }
                }
        }
    }
