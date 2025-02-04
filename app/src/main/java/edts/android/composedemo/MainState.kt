package edts.android.composedemo

import edts.android.composedemo.constants.ThemeMode

data class MainState(
    val theme: ThemeMode = ThemeMode.SYSTEM,
)
