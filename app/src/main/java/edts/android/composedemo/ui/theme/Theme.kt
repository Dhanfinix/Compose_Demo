package edts.android.composedemo.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.utils.AndroidUtil

@Composable
fun ComposeDemoTheme(
    mode: ThemeMode = ThemeMode.SYSTEM,
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when (mode) {
            ThemeMode.DARK -> DarkColorScheme
            ThemeMode.LIGHT -> LightColorScheme
            ThemeMode.CUSTOM -> CustomColorScheme
            ThemeMode.SYSTEM ->
                if (dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    val context = LocalContext.current
                    if (isSystemInDarkTheme()) {
                        dynamicDarkColorScheme(context)
                    } else {
                        dynamicLightColorScheme(context)
                    }
                } else if (isSystemInDarkTheme()) {
                    DarkColorScheme
                } else {
                    LightColorScheme
                }
        }

    AndroidUtil.UpdateSystemBars(mode)

    val typo = if (mode == ThemeMode.CUSTOM) {
        TypographyAllMontserrat
    } else {
        Typography
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typo,
        content = content,
    )
}