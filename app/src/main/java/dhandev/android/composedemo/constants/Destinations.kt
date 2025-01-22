package dhandev.android.composedemo.constants

import kotlinx.serialization.Serializable

sealed class Destinations {
    abstract val title: String

    @Serializable
    data class Home(
        override val title: String = "Home",
    ): Destinations()

    @Serializable
    data class SimpleComponent(
        override val title: String = "Simple Component",
    ): Destinations()

    @Serializable
    data class BasicLayout(
        override val title: String = "Basic Layout",
    ): Destinations()
}