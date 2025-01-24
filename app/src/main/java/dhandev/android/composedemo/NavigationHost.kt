package dhandev.android.composedemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.LocalNavController
import dhandev.android.composedemo.ui.screen.ComposeModifierScreen
import dhandev.android.composedemo.ui.screen.HomeScreen
import dhandev.android.composedemo.ui.screen.SimpleComponentScreen
import dhandev.android.composedemo.ui.screen.StateManagementScreen
import dhandev.android.composedemo.ui.screen.adv_state.AdvStateManagementScreen

@Composable
fun NavigationHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    CompositionLocalProvider(
        LocalNavController provides navController
    ){
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Destinations.Home()
        ){
            composable<Destinations.Home> {
                HomeScreen{
                    navController.navigate(it)
                }
            }
            composable<Destinations.SimpleComponent> {
                SimpleComponentScreen()
            }
            composable<Destinations.ComposeModifier> {
                ComposeModifierScreen()
            }
            composable<Destinations.StateManagement> {
                StateManagementScreen()
            }
            composable<Destinations.AdvStateManagement> {
                AdvStateManagementScreen()
            }
        }
    }

}