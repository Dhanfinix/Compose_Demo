package edts.android.composedemo.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.random.Random

object DpUtils {
    fun getRandomDp(
        heights: List<Dp> = listOf(100.dp, 150.dp, 200.dp, 250.dp, 300.dp)
    ): Dp{
        return heights[Random.nextInt(heights.size)]
    }
}