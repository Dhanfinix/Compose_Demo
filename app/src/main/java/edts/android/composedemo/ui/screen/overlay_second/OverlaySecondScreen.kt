package edts.android.composedemo.ui.screen.overlay_second

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import edts.android.composedemo.MainActivity
import edts.android.composedemo.app_monitor.AppMonitorUsageStatsService
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.ui.screen.overlay.OverlayService
import edts.android.composedemo.utils.AndroidUtil.checkUsageAccessPermission

@Composable
fun OverlaySecondScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as MainActivity
    var hasPermission by remember { mutableStateOf(checkUsageAccessPermission(context)) }
    var hasOverlayPermissions by remember {
        mutableStateOf(Settings.canDrawOverlays(activity))
    }
    val appMonitorIntent by lazy { Intent(activity, AppMonitorUsageStatsService::class.java) }
    var appMonitorActive by remember {
        mutableStateOf(false)
    }
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.OverlaySecond().title
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                enabled = !hasPermission,
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            ) {
                Text("Open Usage Access Settings")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (hasPermission) "✅ Permission Granted" else "❌ Permission Not Granted",
                style = MaterialTheme.typography.bodyLarge
            )

            HorizontalDivider(Modifier.padding(vertical = 8.dp))

            Button(
                enabled = hasPermission,
                onClick = {
                    if (hasOverlayPermissions){
                        if (appMonitorActive){
                            activity.stopService(appMonitorIntent)
                            appMonitorActive = false
                        } else {
                            activity.startService(appMonitorIntent)
                            appMonitorActive = true
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
                        appMonitorActive  -> "Stop App Monitor service"
                        else           -> "Start App Monitor service"
                    }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Overlay Permission: ${if (hasOverlayPermissions) "Granted" else "Not Granted"}"
            )
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = checkUsageAccessPermission(context)
                hasOverlayPermissions = Settings.canDrawOverlays(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    DisposableEffect(Unit) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == OverlayService.OVERLAY_STOP_INTENT) {
                    appMonitorActive = false
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