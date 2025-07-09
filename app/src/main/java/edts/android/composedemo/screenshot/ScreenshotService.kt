package edts.android.composedemo.screenshot

import android.app.*
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PixelFormat
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.Image
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.*
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import androidx.core.graphics.scale
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class ScreenshotService : Service() {

    // A dedicated coroutine scope for background tasks, ensuring they don't block the main thread.
    // SupervisorJob prevents the entire scope from being cancelled if one child coroutine fails.
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private lateinit var mediaProjection: MediaProjection
    private lateinit var virtualDisplay: VirtualDisplay
    private lateinit var imageReader: ImageReader

    private var screenWidth = 0
    private var screenHeight = 0
    private var screenDensity = 0

    private var lastScreenshotHash: Long = 0L

    private val imageProcessingMutex = Mutex()

    // A callback for MediaProjection stopping unexpectedly.
    private val mediaProjectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            Log.d(TAG, "MediaProjection session stopped.")
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())

        val resultCode = intent?.getIntExtra(EXTRA_RESULT_CODE, Activity.RESULT_CANCELED)
        val data = intent?.getParcelableExtra<Intent>(EXTRA_DATA)

        if (resultCode == Activity.RESULT_OK && data != null) {
            setupScreenMetrics()
            startScreenCapture(resultCode, data)
        } else {
            Log.e(TAG, "Failed to get valid screen capture permissions.")
            stopSelf()
        }

        return START_NOT_STICKY
    }

    private fun startScreenCapture(resultCode: Int, data: Intent) {
        val mediaProjectionManager = getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        mediaProjection = mediaProjectionManager.getMediaProjection(resultCode, data)
        mediaProjection.registerCallback(mediaProjectionCallback, null)

        // Setup ImageReader to capture screen content.
        imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2).apply {
            setOnImageAvailableListener({ reader ->
                // Launch a coroutine to process the image off the main thread.
                serviceScope.launch {
                    processImage(reader)
                }
            }, null) // Use a background thread handler by passing null.
        }

        virtualDisplay = mediaProjection.createVirtualDisplay(
            "ScreenshotVirtualDisplay",
            screenWidth,
            screenHeight,
            screenDensity,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader.surface,
            null,
            null
        )
        Log.d(TAG, "Screen capture started.")
    }

    /**
     * Processes an image from the ImageReader on a background thread.
     */
    private suspend fun processImage(reader: ImageReader) {
        // withLock ensures only one coroutine can enter this block at a time.
        // It also safely releases the lock even if an exception occurs.
        imageProcessingMutex.withLock {
            reader.acquireLatestImage()?.use { image ->
                try {
                    val bitmap = imageToBitmap(image)
                    val currentHash = calculateDifferenceHash(bitmap)

                    val changePercentage = if (lastScreenshotHash != 0L) {
                        calculateHashDifference(lastScreenshotHash, currentHash)
                    } else 1.0f

                    if (changePercentage > CHANGE_THRESHOLD) {
                        Log.i(TAG, "Significant change detected: ${(changePercentage * 100).toInt()}%")
                        lastScreenshotHash = currentHash
                        saveForAIAnalysis(bitmap, changePercentage)
                    }

                    bitmap.recycle()
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing screen image", e)
                }
            }
        }
    }

    /**
     * Converts an Image object to a Bitmap.
     */
    private fun imageToBitmap(image: Image): Bitmap {
        val planes = image.planes[0]
        val buffer = planes.buffer
        val pixelStride = planes.pixelStride
        val rowStride = planes.rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = createBitmap(image.width + rowPadding / pixelStride, image.height)
        bitmap.copyPixelsFromBuffer(buffer)

        // If there was padding, create a final cropped bitmap.
        if (rowPadding > 0) {
            val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
            bitmap.recycle()
            return croppedBitmap
        }

        return bitmap
    }

    /**
     * Calculates a Difference Hash (dHash) for the bitmap.
     * dHash is a perceptual hash robust to minor changes.
     */
    private fun calculateDifferenceHash(bitmap: Bitmap): Long {
        // 1. Resize to a small fixed size (e.g., 9x8). 9x8 allows for 8x8 comparisons.
        val smallBitmap = bitmap.scale(DHASH_WIDTH + 1, DHASH_HEIGHT, false)
        var hash = 0L

        for (y in 0 until DHASH_HEIGHT) {
            for (x in 0 until DHASH_WIDTH) {
                // 2. Get grayscale values of adjacent pixels.
                val leftPixel = Color.red(smallBitmap[x, y])
                val rightPixel = Color.red(smallBitmap[x + 1, y])

                // 3. Compare pixels and set a bit.
                hash = hash shl 1
                if (leftPixel > rightPixel) {
                    hash = hash or 1
                }
            }
        }

        smallBitmap.recycle()
        return hash
    }

    /**
     * Calculates the normalized Hamming distance between two hashes.
     * Represents the percentage of bits that are different.
     */
    private fun calculateHashDifference(hash1: Long, hash2: Long): Float {
        // The number of differing bits (Hamming distance).
        val differingBits = (hash1 xor hash2).countOneBits()
        return differingBits / 64.0f
    }

    /**
     * Saves the bitmap to the device's gallery for analysis.
     * This is an I/O operation and should be called from a background coroutine.
     */
    private suspend fun saveForAIAnalysis(bitmap: Bitmap, changePercentage: Float) = withContext(Dispatchers.IO) {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val filename = "AI_Analysis_${timestamp}_${(changePercentage * 100).toInt()}pct.png"

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/AI_Screenshots")
        }

        try {
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            uri?.let {
                contentResolver.openOutputStream(it)?.use { stream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
                    Log.d(TAG, "Saved screenshot: $filename to $uri")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save image", e)
        }
    }

    // --- Boilerplate and Lifecycle ---

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel() // Safely cancel all coroutines.

        // Check if virtualDisplay is initialized before releasing.
        if (::virtualDisplay.isInitialized) {
            virtualDisplay.release()
        }

        // Check if mediaProjection is initialized before unregistering and stopping.
        if (::mediaProjection.isInitialized) {
            mediaProjection.unregisterCallback(mediaProjectionCallback)
            mediaProjection.stop()
        }

        Log.d(TAG, "ScreenshotService destroyed.")
    }

    private fun setupScreenMetrics() {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        screenDensity = resources.displayMetrics.densityDpi

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.navigationBars() or WindowInsets.Type.displayCutout()
            )
            screenWidth = windowMetrics.bounds.width() - insets.left - insets.right
            screenHeight = windowMetrics.bounds.height() - insets.top - insets.bottom
        } else {
            val displayMetrics = DisplayMetrics()
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            screenWidth = displayMetrics.widthPixels
            screenHeight = displayMetrics.heightPixels
        }
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

    companion object {
        private const val TAG = "ScreenshotService"
        private const val NOTIFICATION_ID = 12345
        private const val CHANNEL_ID = "screenshot_service_channel"

        const val EXTRA_RESULT_CODE = "extra_result_code"
        const val EXTRA_DATA = "extra_data"

        // Threshold for detecting a "significant" change (e.g., 5%).
        private const val CHANGE_THRESHOLD = 0.05f

        // dHash dimensions
        private const val DHASH_WIDTH = 8
        private const val DHASH_HEIGHT = 8
    }
}
