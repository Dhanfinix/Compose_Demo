package edts.android.composedemo.ui.screen

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edts.android.composedemo.R
import edts.android.composedemo.constants.DeeplinksPath
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.LocalActivity
import edts.android.composedemo.constants.LocalNavController
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.utils.StringUtils.isValidDeeplink
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val navController = LocalNavController.current
    val context = LocalContext.current
    val intent = if (LocalInspectionMode.current) Intent()
    else LocalActivity.current.intent

    LaunchedEffect(Unit) {
        delay(1500)
        handleDeeplink(navController, context, intent)
    }
    Surface(modifier.fillMaxSize()) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(R.drawable.composedemologo),
                contentDescription = null,
                modifier = Modifier
                    .padding(bottom = 16.dp)
                    .clip(CircleShape)
            )
            Text(
                text = "Compose Demo",
                style = MaterialTheme.typography.headlineLarge
            )
            Text(
                text = "An app to showcase basic of compose",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

private fun handleDeeplink(
    navController: NavController,
    context: Context,
    intent: Intent,
) {
    /** because we directly navigate from SplashScreen, when we press back
     * at destination it will back to SplashScreen and go to default destination
     */
//    navController.navigate(
//        if (isValidDeeplink(context, intent.data)){
//            when (intent.data?.path) {
//                DeeplinksPath.ADV_STATE -> {
//                    val data = intent.data?.getQueryParameter("data") ?: ""
//                    Destinations.AdvStateManagement(data = data)
//                }
//
//                else -> {defaultDestinations}
//            }
//        } else {defaultDestinations}
//    ) {
//        popUpTo(Destinations.Splash()) {
//            inclusive = true
//        }
//    }

    /**
     * Otherwise, this solution have chained destination which make
     * the deeplink destination can back to correct previous destination
     */
    if (isValidDeeplink(context, intent.data)){
        when (intent.data?.path) {
            DeeplinksPath.ADV_STATE -> handleDeeplinkToAdvState(intent, navController)
            else -> handleDefaultDestination(navController)
        }
    } else {
        handleDefaultDestination(navController)
    }

}

private fun handleDefaultDestination(
    navController: NavController
) {
    val defaultDestinations = Destinations.Home()
    navController.navigate(defaultDestinations) {
        popUpTo(Destinations.Splash()) {
            inclusive = true
        }
    }
}

private fun handleDeeplinkToAdvState(
    intent: Intent,
    navController: NavController
) {
    val data = intent.data?.getQueryParameter("data") ?: ""
    val chainedDestination = listOf(
        Destinations.Home(),
        Destinations.StateManagement(),
        Destinations.AdvStateManagement(data = data)
    )
    chainedDestination.forEachIndexed { index, destination ->
        navController.navigate(destination) {
            if (index == 0)
                popUpTo(Destinations.Splash()) {
                    inclusive = true
                }
        }
    }
}

@Preview
@Composable
private fun SplashPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    val navController =
        if (LocalInspectionMode.current)
            rememberNavController()
        else LocalNavController.current
    CompositionLocalProvider(
        LocalNavController provides navController
    ) {
        PreviewWrapperComp(
            themeMode = themeMode,
            content = { SplashScreen() }
        )
    }
}