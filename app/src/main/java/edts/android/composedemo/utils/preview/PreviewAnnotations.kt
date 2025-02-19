package edts.android.composedemo.utils.preview

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.content.res.Configuration.UI_MODE_TYPE_NORMAL
import androidx.compose.ui.tooling.preview.Preview

@Preview(name = "Light")
@Preview(name = "Dark", uiMode = UI_MODE_NIGHT_YES)
annotation class LightDarkPreview

@Preview(
    name = "Small Screen",
    showBackground = true,
    widthDp = 320,
    heightDp = 640
)
@Preview(
    name = "Medium Screen",
    showBackground = true,
    widthDp = 360,
    heightDp = 740
)
@Preview(
    name = "Large Screen",
    showBackground = true,
    widthDp = 411,
    heightDp = 731
)
@Preview(
    name = "Tablet Screen",
    showBackground = true,
    widthDp = 800,
    heightDp = 1280
)
annotation class ScreenSizePreview