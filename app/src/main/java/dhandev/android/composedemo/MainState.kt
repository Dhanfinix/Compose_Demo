package dhandev.android.composedemo

import dhandev.android.composedemo.constants.ThemeMode

data class MainState(
    val theme: ThemeMode = ThemeMode.SYSTEM,
)
