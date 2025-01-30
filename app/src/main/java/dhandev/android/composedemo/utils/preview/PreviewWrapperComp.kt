package dhandev.android.composedemo.utils.preview

import androidx.compose.runtime.Composable
import dhandev.android.composedemo.constants.ThemeMode
import dhandev.android.composedemo.ui.theme.ComposeDemoTheme

@Composable
fun PreviewWrapperComp(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content : @Composable () -> Unit = {}
) {
    ComposeDemoTheme(
        mode = themeMode
    ) {
        content()
    }
}