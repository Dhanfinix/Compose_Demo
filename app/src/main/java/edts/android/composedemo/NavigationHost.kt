package edts.android.composedemo

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.LocalNavController
import edts.android.composedemo.ui.screen.InteroperabilityScreen
import edts.android.composedemo.ui.screen.ComposeModifierScreen
import edts.android.composedemo.ui.screen.HomeScreen
import edts.android.composedemo.ui.screen.RelayScreen
import edts.android.composedemo.ui.screen.SideEffectScreen
import edts.android.composedemo.ui.screen.SimpleComponentScreen
import edts.android.composedemo.ui.screen.SplashScreen
import edts.android.composedemo.ui.screen.state.StateManagementScreen
import edts.android.composedemo.ui.screen.WhyComposeScreen
import edts.android.composedemo.ui.screen.adv_state.AdvStateManagementScreen
import edts.android.composedemo.ui.screen.theming.ThemingScreen

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
            try {
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
                        initialNote = backStackEntry.toRoute<Destinations.AdvStateManagement>().initialNote,
                        deeplinkData = backStackEntry.toRoute<Destinations.AdvStateManagement>().data
                    )
                }
                composable<Destinations.SideEffect> {
                    SideEffectScreen()
                }
                composable<Destinations.Theming> {
                    ThemingScreen()
                }
                composable<Destinations.Interoperability> {
                    InteroperabilityScreen()
                }
                composable<Destinations.WhyCompose> {
                    WhyComposeScreen()
                }
                composable<Destinations.Relay> {
                    RelayScreen()
                }
            } catch (e: IllegalArgumentException){
                Toast.makeText(navController.context, "Destination Not Exist", Toast.LENGTH_SHORT).show()
            }
        }
    }
}