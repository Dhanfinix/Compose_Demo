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
        override val title: String = "Component & Basic Layout",
    ): Destinations()

    @Serializable
    data class ComposeModifier(
        override val title: String = "Modifier",
    ): Destinations()

    @Serializable
    data class StateManagement(
        override val title: String = "State Management",
    ): Destinations()

    @Serializable
    data class AdvStateManagement(
        override val title: String = "Adv. State Management",
    ): Destinations()
}