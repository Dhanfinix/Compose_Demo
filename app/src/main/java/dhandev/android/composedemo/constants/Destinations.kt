package dhandev.android.composedemo.constants

sealed class Destinations {
    abstract val title: String

    data class SimpleComponent(
        override val title: String = "Simple Component",
    ): Destinations()

    data class BasicLayout(
        override val title: String = "Basic Layout",
    ): Destinations()
}