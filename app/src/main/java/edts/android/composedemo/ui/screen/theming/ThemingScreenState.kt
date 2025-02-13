package edts.android.composedemo.ui.screen.theming

import edts.android.composedemo.R
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.component.segment_button.SegmentedData
import edts.android.composedemo.ui.component.segment_button.SegmentedState

data class ThemingScreenState(
    val themeOptions : SegmentedState<ThemeMode> = SegmentedState(
        options = listOf(
            SegmentedData("Light", R.drawable.baseline_light_mode_24, metadata = ThemeMode.LIGHT),
            SegmentedData("Dark", R.drawable.baseline_dark_mode_24, metadata = ThemeMode.DARK),
            SegmentedData("Custom", R.drawable.baseline_person_4_24, metadata = ThemeMode.CUSTOM),
            SegmentedData("System", R.drawable.baseline_auto_mode_24, metadata = ThemeMode.SYSTEM),
        ),
        isMulti = false
    )
)
