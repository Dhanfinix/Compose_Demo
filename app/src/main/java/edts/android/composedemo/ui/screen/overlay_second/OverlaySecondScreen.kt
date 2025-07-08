package edts.android.composedemo.ui.screen.overlay_second

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import edts.android.composedemo.MainActivity
import edts.android.composedemo.app_monitor.AppMonitorStatus
import edts.android.composedemo.app_monitor.AppMonitorUsageStatsService
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.ui.component.DemoScaffoldComp
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
    val isAppMonitorRunning by AppMonitorStatus.isRunning.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Observe permission changes on resume
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

    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.OverlaySecond().title
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            PermissionCard(
                title = "Usage Access Permission",
                description = "Allows this app to monitor app usage in the background. Required to detect when target apps are opened or closed.",
                granted = hasPermission,
                onClick = {
                    context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
                }
            )

            PermissionCard(
                title = "Overlay Permission",
                description = "Allows this app to display floating content over other apps, needed to show real-time overlay while the monitored app is running.",
                granted = hasOverlayPermissions,
                onClick = {
                    val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
                    activity.startActivity(intent)
                }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Start App Monitor Service",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    enabled = hasPermission && hasOverlayPermissions,
                    onClick = {
                        if (isAppMonitorRunning) {
                            activity.stopService(appMonitorIntent)
                        } else {
                            activity.startService(appMonitorIntent)
                        }
                    }
                ) {
                    Text(
                        text = if (isAppMonitorRunning)
                            "🛑 Stop Service"
                        else
                            "▶️ Start Service"
                    )
                }

                Text(
                    text = if (isAppMonitorRunning) "Service is running" else "Service is stopped",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isAppMonitorRunning) Color.Green else Color.Gray,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}


@Composable
fun PermissionCard(
    title: String,
    description: String,
    granted: Boolean,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (granted) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = if (granted) "✅ Granted" else "❌ Not Granted",
                color = if (granted) Color(0xFF388E3C) else Color(0xFFD32F2F),
                style = MaterialTheme.typography.bodyLarge
            )

            if (!granted) {
                Button(onClick = onClick) {
                    Text("Grant Now")
                }
            }
        }
    }
}