package edts.android.composedemo.app_monitor

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import edts.android.composedemo.MainActivity
import edts.android.composedemo.R
import edts.android.composedemo.ui.screen.overlay.OverlayService
import edts.android.composedemo.utils.AndroidUtil.getForegroundApp
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
        "com.bca"
    )

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP_MONITOR) {
            monitoringJob?.cancel()
            stopSelf()
            return START_NOT_STICKY
        }

        val notification = createAppMonitorNotification()
        startForeground(NOTIFICATION_MONITOR_ID, notification)
        monitoringJob = CoroutineScope(Dispatchers.Default).launch {
            var lastWasTarget = false
            while (isActive) {
                val foregroundApp = getForegroundApp(this@AppMonitorUsageStatsService)
                val isNotThisApp = foregroundApp != packageName.toString()
                if (targetPackages.contains(foregroundApp) && !lastWasTarget) {
                    // Target app launched
                    startOverlay()
                    lastWasTarget = true
                } else if (
                    foregroundApp != null &&
                    !targetPackages.contains(foregroundApp) &&
                    lastWasTarget &&
                    isNotThisApp
                ) {
                    // Target app closed
                    stopOverlay()
                    lastWasTarget = false
                }
                delay(3000)
            }
        }

        return START_STICKY
    }

    override fun onDestroy() {
        monitoringJob?.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startOverlay() {
        startService(Intent(applicationContext, OverlayService::class.java))
    }

    private fun stopOverlay() {
        stopService(Intent(applicationContext, OverlayService::class.java))
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