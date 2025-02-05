package edts.android.composedemo.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40
)

val CustomColorScheme = lightColorScheme(
    primary = ColorPrimary50,
    secondary = ColorSecondary50,
    background = ColorSupportBgAlert,
    onBackground = ColorNeutral70,
    surface = ColorNeutral10,
    onSurface = ColorRed50,
    surfaceContainer = ColorNeutral10,
    secondaryContainer = ColorPrimary50,
    onSecondaryContainer = ColorFFF
)