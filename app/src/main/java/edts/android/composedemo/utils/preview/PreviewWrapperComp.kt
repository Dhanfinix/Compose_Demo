package edts.android.composedemo.utils.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import edts.android.composedemo.constants.LocalTheme
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.theme.ComposeDemoTheme

@Composable
fun PreviewWrapperComp(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content : @Composable () -> Unit = {}
) {
    //Composition Local used for any composable that need to access
    //LocalTheme in preview - ex: ThemingScreen
    CompositionLocalProvider(
        LocalTheme provides themeMode
    ) {
        ComposeDemoTheme(
            mode = LocalTheme.current
        ) {
            content()
        }
    }
}