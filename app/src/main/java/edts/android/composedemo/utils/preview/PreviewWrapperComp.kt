package edts.android.composedemo.utils.preview

import androidx.compose.runtime.Composable
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.theme.ComposeDemoTheme

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