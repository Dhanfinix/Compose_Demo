package edts.android.composedemo.screenshot

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowInsets
import android.view.WindowManager

class ScreenMetricsHelper(private val context: Context) {
    fun getScreenMetrics(): ScreenMetrics {
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val density = context.resources.displayMetrics.densityDpi

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val metrics = windowManager.currentWindowMetrics
            val insets = metrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            val width = metrics.bounds.width() - insets.left - insets.right
            val height = metrics.bounds.height() - insets.top - insets.bottom
            ScreenMetrics(width, height, density)
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            ScreenMetrics(displayMetrics.widthPixels, displayMetrics.heightPixels, density)
        }
    }
}

data class ScreenMetrics(val width: Int, val height: Int, val density: Int)
