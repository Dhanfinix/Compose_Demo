package edts.android.composedemo.app_monitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import edts.android.composedemo.MainActivity
import edts.android.composedemo.R
import edts.android.composedemo.screenshot.MediaProjectionPermissionHolder
import edts.android.composedemo.screenshot.ScreenshotService
import edts.android.composedemo.ui.screen.overlay.OverlayService
import edts.android.composedemo.utils.AndroidUtil.getForegroundAppHybrid
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppMonitorUsageStatsService : Service() {
    private var monitoringJob: Job? = null
    private val targetPackages = setOf(
        "com.gojek.gopay",
        "ovo.id",
        "mypoin.indomaret.android",
        "com.bca",
        "id.co.bri.brimo",
        "com.android.chrome",
        "id.dana"
    )

    override fun onCreate() {
        super.onCreate()
        AppMonitorStatus.markRunning()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_MONITOR) {
            monitoringJob?.cancel()
            stopSelf()
            return START_NOT_STICKY
        }

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        val notification = createAppMonitorNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                NOTIFICATION_MONITOR_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(
                NOTIFICATION_MONITOR_ID,
                notification
            )
        }

        var lastValidApp: String? = null
        var inTarget = false

        monitoringJob = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                val currentApp = getForegroundAppHybrid(this@AppMonitorUsageStatsService)

                // Simpan app valid terakhir (non-null, bukan diri sendiri)
                if (!currentApp.isNullOrEmpty() && currentApp != packageName) {
                    lastValidApp = currentApp
                }

                val isTarget = targetPackages.contains(currentApp)
                val isSelf = currentApp == packageName
                val isNull = currentApp == null

                // Debug log
                Log.d("AppMonitor", "current=$currentApp, lastValid=$lastValidApp, inTarget=$inTarget")

                when {
                    isTarget && !inTarget -> {
                        startOverlay()
                        inTarget = true
                    }

                    // Hanya stop jika: current app valid & bukan target
                    (!isTarget && !isSelf && !isNull && inTarget) -> {
                        stopOverlay()
                        inTarget = false
                    }

                    // Optional fallback: jika currentApp == null cukup lama (mis. 2x loop)
                    (isNull && !targetPackages.contains(lastValidApp) && inTarget) -> {
                        stopOverlay()
                        inTarget = false
                    }
                }

                if (powerManager.isInteractive){
                    delay(2_000)
                } else {
                    delay(10_000)
                }
            }
        }


        return START_STICKY
    }

    override fun onDestroy() {
        monitoringJob?.cancel()
        AppMonitorStatus.markStopped()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startOverlay() {
        // Use a 'let' block for safer handling of nullable properties
        MediaProjectionPermissionHolder.dataIntent?.let { data ->
            val resultCode = MediaProjectionPermissionHolder.resultCode!! // Assuming resultCode is always valid if data is not null

            val serviceIntent = Intent(applicationContext, ScreenshotService::class.java).apply {
                // Use the constants defined in the service for type-safety
                putExtra(ScreenshotService.EXTRA_RESULT_CODE, resultCode)
                putExtra(ScreenshotService.EXTRA_DATA, data)
            }

            // CRITICAL: Use startForegroundService for services that call startForeground()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(serviceIntent)
            } else {
                // Fallback for older Android versions
                startService(serviceIntent)
            }

        } ?: run {
            // This block runs if the permission data is not available
            Toast.makeText(applicationContext, "Permission data is missing. Please grant permission again.", Toast.LENGTH_LONG).show()
        }
    }

    private fun stopOverlay() {
        stopService(Intent(applicationContext, ScreenshotService::class.java))
    }

    private fun createAppMonitorNotification(): Notification {
        val channelId = "app_monitor_channel"
        val channelName = "App Monitor Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        val stopIntent = Intent(this, AppMonitorUsageStatsService::class.java).apply {
            action = ACTION_STOP_MONITOR
        }

        val pendingStopIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val launchIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val launchPendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )


        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("App Monitor Active")
            .setContentText("Tap to return to app")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setContentIntent(launchPendingIntent)
            .addAction(
                R.drawable.outline_stop_circle_24,
                "Stop Monitoring",
                pendingStopIntent
            )
            .build()
    }

    companion object{
        const val NOTIFICATION_MONITOR_ID = 102
        const val ACTION_STOP_MONITOR = "edts.android.composedemo.app_monitor.STOP_MONITOR"
    }
}