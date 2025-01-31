package dhandev.android.composedemo.ui.screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import dhandev.android.composedemo.R
import dhandev.android.composedemo.constants.DeeplinksPath
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.LocalActivity
import dhandev.android.composedemo.constants.LocalNavController
import dhandev.android.composedemo.constants.ThemeMode
import dhandev.android.composedemo.utils.preview.PreviewWrapperComp
import dhandev.android.composedemo.utils.preview.ThemePreviewProvider
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier
) {
    val navController = if (LocalInspectionMode.current)
        rememberNavController()
        else LocalNavController.current
    val intent = if (LocalInspectionMode.current) Intent()
    else LocalActivity.current.intent
    val schemes = stringResource(R.string.schemes)
    val host = stringResource(R.string.host)

    LaunchedEffect(Unit) {
        delay(1500)
        handleDeeplink(navController, intent, schemes, host)
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
    intent: Intent,
    schemes: String,
    host: String
) {
    val defaultDestinations = Destinations.Home()
    navController.navigate(
        if (intent.data?.scheme == schemes && intent.data?.host == host){
            when (intent.data?.path) {
                DeeplinksPath.ADV_STATE -> {
                    val data = intent.data?.getQueryParameter("data") ?: ""
                    Destinations.AdvStateManagement(data = data)
                }

                else -> {defaultDestinations}
            }
        } else {defaultDestinations}
    ) {
        popUpTo(Destinations.Splash()) {
            inclusive = true
        }
    }
}

@Preview
@Composable
private fun SplashPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = { SplashScreen() }
    )
}