package dhandev.android.composedemo.constants

import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

val LocalNavController = compositionLocalOf<NavController> {
    error("No NavController found!")
}