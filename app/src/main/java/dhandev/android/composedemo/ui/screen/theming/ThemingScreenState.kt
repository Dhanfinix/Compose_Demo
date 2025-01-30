package dhandev.android.composedemo.ui.screen.theming

import dhandev.android.composedemo.R
import dhandev.android.composedemo.constants.ThemeMode
import dhandev.android.composedemo.ui.component.segment_button.SegmentedData
import dhandev.android.composedemo.ui.component.segment_button.SegmentedState

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
