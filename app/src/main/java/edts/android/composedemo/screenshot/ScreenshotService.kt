package edts.android.composedemo.screenshot

import android.app.Activity
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class ScreenshotService : Service() {
    // A dedicated coroutine scope for background tasks, ensuring they don't block the main thread.
    // SupervisorJob prevents the entire scope from being cancelled if one child coroutine fails.
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var imageReader: ImageReader

    private lateinit var imageProcessor: ImageProcessor

    private val mediaProjectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            Log.d(TAG, "MediaProjection session stopped.")
            stopSelf()
        }
    }

    override fun onCreate() {
        super.onCreate()
        imageProcessor = ImageProcessor(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
        val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableExtra(EXTRA_DATA, Intent::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_DATA)
        }

        if (resultCode == Activity.RESULT_OK && data != null) {
            val screenMetrics = ScreenMetricsHelper(this).getScreenMetrics()
            setupProjection(resultCode, data, screenMetrics)
        } else {
            Log.e(TAG, "Failed to get valid screen capture permissions.")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun setupProjection(resultCode: Int, data: Intent, metrics: ScreenMetrics) {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
        mediaProjection.registerCallback(mediaProjectionCallback, null)

        imageReader = ImageReader.newInstance(metrics.width, metrics.height, PixelFormat.RGBA_8888, 2).apply {
            setOnImageAvailableListener({ reader ->
                serviceScope.launch {
                    imageProcessor.processImage(reader)
                }
            }, null)
        }

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenshotVirtualDisplay",
            metrics.width,
            metrics.height,
            metrics.density,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )

        Log.d(TAG, "Screen capture started.")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Screenshot Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AI Screen Analyzer")
            .setContentText("Monitoring screen for changes.")
            .setSmallIcon(android.R.drawable.ic_menu_camera)
            .setOngoing(true)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        if (::virtualDisplay.isInitialized) virtualDisplay.release()
        if (::mediaProjection.isInitialized) {
            mediaProjection.unregisterCallback(mediaProjectionCallback)
            mediaProjection.stop()
        }
        Log.d(TAG, "ScreenshotService destroyed.")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val TAG = "ScreenshotService"
        private const val NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "screenshot_service_channel"

        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_DATA = "extra_data"
    }
}

