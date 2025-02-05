package edts.android.composedemo.constants

import android.app.Activity
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController found!")
}

val LocalActivity = compositionLocalOf<Activity> {
    error("No Activity found!")
}

val LocalTheme = compositionLocalOf<ThemeMode> {
    error("No Theme found!")
}