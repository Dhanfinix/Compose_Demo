package edts.android.composedemo.ui.screen

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import edts.android.composedemo.MainActivity
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.overlay.OverlayService
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.utils.AndroidUtil

@Composable
fun OverlayScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as MainActivity
    var hasOverlayPermissions by remember {
        mutableStateOf(Settings.canDrawOverlays(context))
    }
    val overlayIntent by lazy { Intent(activity, OverlayService::class.java) }
    var overlayActive by remember {
        mutableStateOf(false)
    }
    var hasAccessibilityPermission by remember {
        mutableStateOf(AndroidUtil.getAccessibilityEnabled(context))
    }

    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.Overlay().title
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Accessibility Permission: ${if (hasAccessibilityPermission) "Granted" else "Not Granted"}"
            )
            Spacer(Modifier.height(6.dp))
            Button(
                enabled = !hasAccessibilityPermission,
                onClick = {
                    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                    context.startActivity(intent)
                }
            ){
                Text(
                    text = if(!hasAccessibilityPermission)
                        "Grant accessibility permission"
                    else
                        "Accessibility permission granted"
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Text(
                text = "Overlay Permission: ${if (hasOverlayPermissions) "Granted" else "Not Granted"}"
            )
            Spacer(Modifier.height(6.dp))
            Button(
                enabled = hasAccessibilityPermission,
                onClick = {
                    if (hasOverlayPermissions){
                        if (overlayActive){
                            activity.stopService(overlayIntent)
                            overlayActive = false
                        } else {
                            activity.startService(overlayIntent)
                            overlayActive = true
                        }
                    } else {
                        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                        activity.startActivity(intent)
                    }
                }
            ) {
                Text(
                    text = when {
                        !hasOverlayPermissions -> "Grant overlay permission"
                        overlayActive  -> "Hide overlay"
                        else           -> "Show overlay"
                    }
                )
            }
        }
    }

    // Refresh status when we resume from Settings screen
    LaunchedEffect(Unit) {
        snapshotFlow { activity.lifecycle.currentState }
            .collect {
                hasOverlayPermissions = Settings.canDrawOverlays(context)
                hasAccessibilityPermission = AndroidUtil.getAccessibilityEnabled(context)
            }
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == OverlayService.OVERLAY_STOP_INTENT) {
                    overlayActive = false
                }
            }
        }

        val filter = IntentFilter(OverlayService.OVERLAY_STOP_INTENT)
        ContextCompat.registerReceiver(
            activity,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose {
            activity.unregisterReceiver(receiver)
        }
    }

}