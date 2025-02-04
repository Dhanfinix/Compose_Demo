package edts.android.composedemo.utils.preview

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.theme.ComposeDemoTheme

class ThemePreviewProvider : PreviewParameterProvider<ThemeMode> {
    override val values = sequenceOf(
        ThemeMode.LIGHT,
        ThemeMode.DARK,
        ThemeMode.CUSTOM,
        ThemeMode.SYSTEM,
    )
}

/**
 * To easily create this kind of preview,
 * use Live Template "tprev"
 */
@Preview
@Composable
private fun MyPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    ComposeDemoTheme(
        mode = themeMode
    ) { 
        Button(onClick = {}) {
            Text("Any Button")
        }
    }
}