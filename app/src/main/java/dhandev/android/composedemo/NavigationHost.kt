package dhandev.android.composedemo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.LocalNavController
import dhandev.android.composedemo.ui.screen.ComposeModifierScreen
import dhandev.android.composedemo.ui.screen.HomeScreen
import dhandev.android.composedemo.ui.screen.SideEffectScreen
import dhandev.android.composedemo.ui.screen.SimpleComponentScreen
import dhandev.android.composedemo.ui.screen.SplashScreen
import dhandev.android.composedemo.ui.screen.StateManagementScreen
import dhandev.android.composedemo.ui.screen.adv_state.AdvStateManagementScreen

@Composable
fun NavigationHost(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val baseUriDeeplink = "${stringResource(R.string.schemes)}://${stringResource(R.string.host)}"

    CompositionLocalProvider(
        LocalNavController provides navController
    ){
        NavHost(
            modifier = modifier,
            navController = navController,
            startDestination = Destinations.Splash()
        ){
            composable<Destinations.Splash>(
                deepLinks = listOf(navDeepLink<Destinations.Splash>(baseUriDeeplink))
            ) {
                SplashScreen()
            }
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
            /** this deeplink can be used, but it wont go through splash screen,
             * so the solution is make the splash screen as single source of truth
             * for deeplink, then navigate to target screen using nav controller.
             * deepLinks = listOf(
             *  navDeepLink<Destinations.AdvStateManagement>("${baseUriDeeplink}/adv-state"),
             *  //ex usage: composedemo://show/adv-state?data=test
             *  navDeepLink<Destinations.AdvStateManagement>("${baseUriDeeplink}/adv-state?{data}")
             * )
             */
            composable<Destinations.AdvStateManagement> {backStackEntry->
                AdvStateManagementScreen(
                    deeplinkData = backStackEntry.toRoute<Destinations.AdvStateManagement>().data
                )
            }
            composable<Destinations.SideEffect> {
                SideEffectScreen()
            }
        }
    }
}