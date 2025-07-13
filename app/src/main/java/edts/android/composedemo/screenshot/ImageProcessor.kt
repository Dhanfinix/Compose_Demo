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
                            if (lastScreenType != ScreenType.PAYMENT) {
                                startOverlay()
                            } else {
                                Log.d(TAG, "Still in Payment screen — skipping overlay start.")
                            }
                            lastScreenType = ScreenType.PAYMENT
                        } else {
                            stopOverlay()
                            lastScreenType = ScreenType.NOT_PAYMENT
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
        context.startService(Intent(context, OverlayService::class.java))
    }

    private fun stopOverlay() {
        context.stopService(Intent(context, OverlayService::class.java))
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
        val assetFileDescriptor = context.assets.openFd("mobilenetv3_payment_screen.tflite")
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
        private const val CHANGE_THRESHOLD = 0.05f
        private const val DHASH_WIDTH = 8
        private const val DHASH_HEIGHT = 8
    }
} 
