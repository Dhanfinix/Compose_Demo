package edts.android.composedemo.constants

import android.app.Activity
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController

// Don't forget to add default provider for preview, add it on PreviewWrapperComp

val LocalNavController = compositionLocalOf<NavController> {
    noLocalProvidedFor("LocalNavController")
}

val LocalActivity = compositionLocalOf<Activity?> {
    noLocalProvidedFor("LocalActivity")
}

val LocalTheme = compositionLocalOf<ThemeMode> {
    noLocalProvidedFor("LocalTheme")
}

val LocalScreenName = compositionLocalOf {
    "Empty Screen Name"
}

fun noLocalProvidedFor(name: String): Nothing {
    error("CompositionLocal $name not present")
}