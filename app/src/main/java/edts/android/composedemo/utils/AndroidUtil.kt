package edts.android.composedemo.utils

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import edts.android.composedemo.constants.ThemeMode

object AndroidUtil {
    @Composable
    fun UpdateSystemBars(themeMode: ThemeMode) {
        val context = LocalContext.current
        val activity = context as? Activity
        val window = activity?.window ?: return
        val view = LocalView.current

        val useDarkIcons =
            themeMode == ThemeMode.LIGHT || themeMode == ThemeMode.CUSTOM ||
                (themeMode == ThemeMode.SYSTEM && !isSystemInDarkTheme())

        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        windowInsetsController.isAppearanceLightStatusBars = useDarkIcons
        windowInsetsController.isAppearanceLightNavigationBars = useDarkIcons
    }
}
