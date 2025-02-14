package edts.android.composedemo.utils.preview

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.navigation.compose.rememberNavController
import edts.android.composedemo.constants.LocalActivity
import edts.android.composedemo.constants.LocalNavController
import edts.android.composedemo.constants.LocalTheme
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.theme.ComposeDemoTheme

/**
 * DO NOT USE AS COMPONENT WRAPPER, ONLY FOR PREVIEW
 *
 * This preview wrapper accept theme mode and
 * already provides custom composition local provider value for preview
 *
 *
 * @param themeMode, use @PreviewParameter(ThemePreviewProvider::class) to preview in all themes
 * @param content, content to be preview
 */
@Composable
fun PreviewWrapperComp(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    content : @Composable () -> Unit = {}
) {
    CompositionLocalProvider(
        LocalTheme provides themeMode,
        LocalNavController provides rememberNavController(),
        LocalActivity provides null
    ) {
        ComposeDemoTheme(
            mode = LocalTheme.current
        ) {
            content()
        }
    }
}