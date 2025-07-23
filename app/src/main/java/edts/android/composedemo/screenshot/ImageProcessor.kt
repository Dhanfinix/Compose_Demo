package edts.android.composedemo.screenshot

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.Image
import android.media.ImageReader
import android.util.Log
import androidx.core.graphics.createBitmap
import androidx.core.graphics.get
import androidx.core.graphics.scale
import edts.android.composedemo.ui.screen.overlay.OverlayService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.tensorflow.lite.Interpreter
import java.nio.channels.FileChannel
import java.util.Locale

class ImageProcessor(private val context: Context) {
    private var lastScreenshotHash: Long = 0L
    private val imageProcessingMutex = Mutex()
    private lateinit var tflite: Interpreter
    private var inputImageWidth = 0
    private var inputImageHeight = 0
    private var lastScreenType: ScreenType? = null
    private var isOverlayRunning = false
    private val currencyExtractor = CurrencyExtractor()
    private var lastSentNominal: String? = null // Track last sent nominal to prevent duplicates
    private var lastCurrencyExtractionTime = 0L
    private val currencyExtractionInterval = 0L // 500ms between currency extractions

    init {
        loadModel()
    }

    suspend fun processImage(reader: ImageReader) {
        imageProcessingMutex.withLock {
            reader.acquireLatestImage()?.use { image ->
                try {
                    val bitmap = imageToBitmap(image)
                    val currentHash = calculateDifferenceHash(bitmap)
                    val changePercentage = if (lastScreenshotHash != 0L) {
                        calculateHashDifference(lastScreenshotHash, currentHash)
                    } else 1.0f

                    if (changePercentage > CHANGE_THRESHOLD) {
                        lastScreenshotHash = currentHash
                        val isPayment = isPaymentScreen(bitmap)

                        if (isPayment) {
                            val currentTime = System.currentTimeMillis()
                            val timeSinceLast = currentTime - lastCurrencyExtractionTime

                            // Only process currency if enough time has passed or it's the first payment screen
                            if (lastScreenType != ScreenType.PAYMENT || timeSinceLast >= currencyExtractionInterval) {
                                lastScreenType = ScreenType.PAYMENT
                                // Only process if not already processing to prevent overlapping requests
                                if (!currencyExtractor.isCurrentlyProcessing()) {
                                    lastCurrencyExtractionTime = currentTime

                                    // Create a safe copy before processing (don't recycle original bitmap yet)
                                    val safeBitmap = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, false)
                                    if (safeBitmap != null) {
                                        CoroutineScope(Dispatchers.Main).launch {
                                            currencyExtractor.extractFromBitmap(
                                                bitmap = safeBitmap,
                                                onSuccess = { nominal ->
                                                    // Only send if it's different from the last sent nominal
                                                    if (lastSentNominal != nominal) {
                                                        lastSentNominal = nominal
                                                        sendToOverlay(nominal)
                                                    } else {
                                                        Log.d(TAG, "Same nominal as before, not updating overlay: $nominal")
                                                    }
                                                },
                                                onFailure = { error ->
                                                    Log.e(TAG, "Currency extraction failed: $error")
                                                    // Only stop overlay if we haven't sent a nominal before
                                                    if (lastSentNominal == null) {
                                                        stopOverlay()
                                                    }
                                                },
                                                onRejected = { value, reason ->
                                                    Log.d(TAG, "Currency rejected: $value - $reason")
                                                    // Send rejected value for debugging but don't stop overlay
                                                    sendToOverlay(value, reason)
                                                }
                                            )
                                        }
                                    } else {
                                        Log.e(TAG, "Failed to create safe bitmap copy for currency extraction")
                                    }
                                } else {
                                    Log.d(TAG, "Currency extractor busy, skipping this frame")
                                }
                            } else {
                                lastScreenType = ScreenType.PAYMENT
                                Log.d(TAG, "Currency extraction throttled (${timeSinceLast}ms < ${currencyExtractionInterval}ms)")
                            }
                        } else {
                            // Screen changed from payment to non-payment
                            if (lastScreenType == ScreenType.PAYMENT) {
                                lastScreenType = ScreenType.NOT_PAYMENT
                                Log.d(TAG, "Screen changed from payment to non-payment, stopping overlay")
                                currencyExtractor.reset() // Reset extractor state
                                stopOverlay()
                                lastSentNominal = null
                                lastCurrencyExtractionTime = 0L // Reset extraction timing
                            }
                        }
                    }

                    bitmap.recycle()
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing screen image", e)
                }
            }
        }
    }

    private fun startOverlay() {
        if (!isOverlayRunning) {
            context.startService(Intent(context, OverlayService::class.java))
            isOverlayRunning = true
            Log.d(TAG, "Overlay service started")
        }
    }

    private fun stopOverlay() {
        if (isOverlayRunning) {
            context.stopService(Intent(context, OverlayService::class.java))
            isOverlayRunning = false
            lastSentNominal = null // Clear last sent nominal when stopping
            Log.d(TAG, "Overlay service stopped")
        }
    }

    private fun sendToOverlay(nominal: String, reason: String? = null) {
        if (lastScreenType == ScreenType.PAYMENT){
            if (!isOverlayRunning) {
                startOverlay()
                // Wait a bit for service to initialize before sending update
                CoroutineScope(Dispatchers.Main).launch {
                    delay(200) // Slightly longer delay for service initialization
                    sendUpdateIntent(nominal, reason)
                }
            } else {
                sendUpdateIntent(nominal, reason)
            }
        }
    }

    private fun sendUpdateIntent(nominal: String, reason: String?) {
        val intent = Intent(context, OverlayService::class.java).apply {
            action = OverlayService.UPDATE_ACTION
            putExtra(OverlayService.NOMINAL, nominal)
            putExtra(OverlayService.REASON, reason)
        }
        context.startService(intent)
        Log.d(TAG, "Update intent sent - Nominal: $nominal, Reason: $reason")
    }

    private fun imageToBitmap(image: Image): Bitmap {
        val planes = image.planes[0]
        val buffer = planes.buffer
        val pixelStride = planes.pixelStride
        val rowStride = planes.rowStride
        val rowPadding = rowStride - pixelStride * image.width

        val bitmap = createBitmap(image.width + rowPadding / pixelStride, image.height)
        bitmap.copyPixelsFromBuffer(buffer)

        return if (rowPadding > 0) {
            val croppedBitmap = Bitmap.createBitmap(bitmap, 0, 0, image.width, image.height)
            bitmap.recycle()
            croppedBitmap
        } else bitmap
    }

    private fun calculateDifferenceHash(bitmap: Bitmap): Long {
        val smallBitmap = bitmap.scale(DHASH_WIDTH + 1, DHASH_HEIGHT, false)
        var hash = 0L
        for (y in 0 until DHASH_HEIGHT) {
            for (x in 0 until DHASH_WIDTH) {
                val left = Color.red(smallBitmap[x, y])
                val right = Color.red(smallBitmap[x + 1, y])
                hash = (hash shl 1) or if (left > right) 1 else 0
            }
        }
        smallBitmap.recycle()
        return hash
    }

    private fun calculateHashDifference(hash1: Long, hash2: Long): Float {
        val differingBits = (hash1 xor hash2).countOneBits()
        return differingBits / 64.0f
    }

    private fun loadModel() {
        val assetFileDescriptor = context.assets.openFd("mobilenetv3_payment_screen_fp16.tflite")
        val fileInputStream = assetFileDescriptor.createInputStream()
        val fileChannel = fileInputStream.channel
        val modelBuffer = fileChannel.map(
            FileChannel.MapMode.READ_ONLY,
            assetFileDescriptor.startOffset,
            assetFileDescriptor.declaredLength
        )
        tflite = Interpreter(modelBuffer)
        val inputShape = tflite.getInputTensor(0).shape()
        inputImageHeight = inputShape[1]
        inputImageWidth = inputShape[2]
    }

    private fun isPaymentScreen(bitmap: Bitmap): Boolean {
        val resized = resizeWithAspectRatio(bitmap)
        val cropped = centerCrop(resized, inputImageWidth, inputImageHeight)
        val mean = floatArrayOf(0.485f, 0.456f, 0.406f)
        val std = floatArrayOf(0.229f, 0.224f, 0.225f)

        val input = Array(1) { Array(inputImageHeight) { Array(inputImageWidth) { FloatArray(3) } } }
        for (y in 0 until inputImageHeight) {
            for (x in 0 until inputImageWidth) {
                val px = cropped[x, y]
                input[0][y][x][0] = ((Color.red(px) / 255.0f) - mean[0]) / std[0]
                input[0][y][x][1] = ((Color.green(px) / 255.0f) - mean[1]) / std[1]
                input[0][y][x][2] = ((Color.blue(px) / 255.0f) - mean[2]) / std[2]
            }
        }

        val output = Array(1) { FloatArray(ScreenType.entries.size) }
        tflite.run(input, output)

        val probabilities = output[0]
        val maxIndex = probabilities.indices.maxByOrNull { probabilities[it] } ?: -1
        val label = ScreenType.entries[maxIndex]
        val confidence = probabilities[maxIndex]

        Log.d(TAG, "Prediction: $label (${String.format(Locale.getDefault(), "%.2f", confidence * 100)}%)")
        return label == ScreenType.PAYMENT
    }

    private fun resizeWithAspectRatio(
        bitmap: Bitmap,
        targetSize: Int = 256
    ): Bitmap {
        val (w, h) = bitmap.width to bitmap.height
        return if (w < h) bitmap.scale(targetSize, targetSize * h / w)
        else bitmap.scale(targetSize * w / h, targetSize)
    }

    private fun centerCrop(bitmap: Bitmap, cropW: Int, cropH: Int): Bitmap {
        val startX = (bitmap.width - cropW) / 2
        val startY = (bitmap.height - cropH) / 2
        return Bitmap.createBitmap(bitmap, startX, startY, cropW, cropH)
    }

    companion object {
        private const val TAG = "ImageProcessor"
        private const val CHANGE_THRESHOLD = 0.015f
        private const val DHASH_WIDTH = 8
        private const val DHASH_HEIGHT = 8
    }
} 
