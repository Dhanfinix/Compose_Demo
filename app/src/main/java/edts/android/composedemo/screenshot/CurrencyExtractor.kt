package edts.android.composedemo.screenshot

import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class CurrencyExtractor {

    companion object {
        private const val TAG = "CurrencyExtractor"
        private const val TARGET_WIDTH = 720
        private const val MIN_CURRENCY_AMOUNT = 0L
        private const val MAX_CURRENCY_AMOUNT = 10_000_000_000L
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    private val processingMutex = Mutex()
    private var isProcessing = false
    private var lastProcessedResult: String? = null
    private var lastProcessedTimestamp = 0L
    private val minProcessingInterval = 100L // 100ms minimum between processing

    private data class CurrencyCandidate(
        val original: String,
        val cleaned: String,
        val numeric: Long,
        val priority: Int,
        val confidence: Double
    )

    suspend fun extractFromBitmap(
        bitmap: Bitmap,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
        onRejected: (String, String) -> Unit
    ) {
        processingMutex.withLock {
            val currentTime = System.currentTimeMillis()
            val timeSinceLastProcess = currentTime - lastProcessedTimestamp

            // Skip if already processing
            if (isProcessing) {
                Log.d(TAG, "⏳ Skipping - OCR already in progress")
                return
            }

            // Skip if too frequent (but allow first call)
            if (lastProcessedTimestamp > 0 && timeSinceLastProcess < minProcessingInterval) {
                Log.d(TAG, "⏳ Skipping - too frequent (${timeSinceLastProcess}ms < ${minProcessingInterval}ms)")
                return
            }

            // Validate bitmap before processing
            if (bitmap.isRecycled) {
                Log.e(TAG, "❌ Bitmap is recycled, cannot process")
                onFailure("Bitmap is recycled")
                return
            }

            isProcessing = true
            lastProcessedTimestamp = currentTime

            try {
                // Create a safe copy of the bitmap for OCR processing
                val bitmapCopy = bitmap.copy(bitmap.config ?: Bitmap.Config.ARGB_8888, false)
                if (bitmapCopy == null) {
                    Log.e(TAG, "❌ Failed to create bitmap copy")
                    onFailure("Failed to create bitmap copy")
                    isProcessing = false
                    return
                }

                val resized = bitmapCopy.scale(
                    TARGET_WIDTH,
                    (bitmapCopy.height * TARGET_WIDTH.toFloat() / bitmapCopy.width).toInt()
                )

                // Clean up the copy if it's different from resized
                if (resized != bitmapCopy) {
                    bitmapCopy.recycle()
                }

                val inputImage = InputImage.fromBitmap(resized, 0)

                recognizer.process(inputImage)
                    .addOnSuccessListener { visionText ->
                        try {
                            val extractedNominal = extractCurrencyNominal(visionText.text, onRejected)

                            extractedNominal?.let { nominal ->
                                // Check if this is the same result as last time to prevent duplicate overlays
                                if (lastProcessedResult != nominal) {
                                    Log.d(TAG, "💰 New currency nominal extracted: $nominal")
                                    lastProcessedResult = nominal
                                    onSuccess(nominal)
                                } else {
                                    Log.d(TAG, "🔄 Same currency nominal as before, skipping: $nominal")
                                }
                            } ?: run {
                                Log.d(TAG, "💸 No valid currency nominal found.")
                                lastProcessedResult = null
                                onFailure("No valid currency nominal found")
                            }
                        } catch (e: Exception) {
                            Log.e(TAG, "Error in success callback: ${e.message}", e)
                            onFailure("Error processing OCR result: ${e.message}")
                        } finally {
                            // Clean up resized bitmap
                            if (!resized.isRecycled) {
                                resized.recycle()
                            }
                            isProcessing = false
                        }
                    }
                    .addOnFailureListener { e ->
                        try {
                            Log.e(TAG, "OCR Failed: ${e.message}")
                            lastProcessedResult = null
                            onFailure("OCR Failed: ${e.message}")
                        } finally {
                            // Clean up resized bitmap
                            if (!resized.isRecycled) {
                                resized.recycle()
                            }
                            isProcessing = false
                        }
                    }
                    .addOnCompleteListener {
                        // Ensure isProcessing is always reset and bitmap is cleaned up
                        isProcessing = false
                        if (!resized.isRecycled) {
                            resized.recycle()
                        }
                    }

            } catch (e: Exception) {
                Log.e(TAG, "Error setting up OCR: ${e.message}", e)
                isProcessing = false
                onFailure("Error setting up OCR: ${e.message}")
            }
        }
    }

    private fun extractCurrencyNominal(text: String, onRejected: (String, String) -> Unit): String? {
        if (text.isBlank()) {
            Log.d(TAG, "📝 Empty text received from OCR")
            return null
        }

        Log.d(TAG, "📝 Processing OCR text: ${text.take(100)}...") // Log first 100 chars

        val patterns = listOf(
            // Rp 50.000 or Rp50.000 or Rp 50,000 - highest priority
            Regex("""(?:Rp\.?\s*)([\d.,]+)""", RegexOption.IGNORE_CASE),
            // IDR 50000 or IDR 50.000 - second priority
            Regex("""(?:IDR\s*)([\d.,]+)""", RegexOption.IGNORE_CASE)
        )

        val candidates = mutableListOf<CurrencyCandidate>()
        val rejectedCandidates = mutableSetOf<String>() // Prevent duplicate rejection logs

        patterns.forEachIndexed { priority, pattern ->
            pattern.findAll(preprocessOcrText(text)).forEach { match ->
                val rawValue = match.groupValues.getOrNull(1) ?: match.value
                val cleanValue = cleanCurrencyValue(rawValue)
                val numericValue = parseNumericValue(cleanValue)

                if (isValidCurrencyAmount(numericValue)) {
                    candidates.add(
                        CurrencyCandidate(
                            original = match.value,
                            cleaned = formatCurrency(numericValue),
                            numeric = numericValue,
                            priority = priority,
                            confidence = calculateConfidence(match.value, numericValue)
                        )
                    )
                    Log.d(TAG, "✅ Valid candidate: ${match.value} -> ${formatCurrency(numericValue)} (confidence: ${calculateConfidence(match.value, numericValue)})")
                } else if (!rejectedCandidates.contains(match.value)) {
                    rejectedCandidates.add(match.value)
                    val reason = when {
                        numericValue < MIN_CURRENCY_AMOUNT -> "Below minimum amount (${formatCurrency(MIN_CURRENCY_AMOUNT)})"
                        numericValue > MAX_CURRENCY_AMOUNT -> "Above maximum amount (${formatCurrency(MAX_CURRENCY_AMOUNT)})"
                        numericValue == 0L -> "Invalid number format"
                        else -> "Invalid amount"
                    }
                    onRejected(match.value, reason)
                    Log.d(TAG, "🚫 Rejected: ${match.value} -> $numericValue - $reason")
                }
            }
        }

        val bestCandidate = selectBestCandidate(candidates)
        bestCandidate?.let {
            Log.d(TAG, "🏆 Best candidate selected: ${it.original} -> ${it.cleaned} (priority: ${it.priority}, confidence: ${it.confidence})")
        }

        return bestCandidate?.cleaned
    }

    private fun cleanCurrencyValue(value: String): String {
        if (value.isBlank()) return ""

        // Remove all non-digit characters except dots and commas
        val digitsOnly = value.replace(Regex("""[^\d.,]"""), "")

        if (digitsOnly.isBlank()) return ""

        // Handle Indonesian currency format (dots as thousand separators, commas rare)
        return when {
            // If contains both dots and commas, assume dot as thousand separator
            digitsOnly.contains(".") && digitsOnly.contains(",") -> {
                digitsOnly.replace(".", "").replace(",", "")
            }
            // If multiple dots, assume thousand separators
            digitsOnly.count { it == '.' } > 1 -> {
                digitsOnly.replace(".", "")
            }
            // If dot with exactly 3 digits after, might be thousand separator (e.g., 50.000)
            Regex("""\d+\.\d{3}$""").matches(digitsOnly) -> {
                digitsOnly.replace(".", "")
            }
            // If dot with 1-2 digits after, might be decimal (rare in Indonesian currency)
            Regex("""\d+\.\d{1,2}$""").matches(digitsOnly) -> {
                digitsOnly.replace(".", "")
            }
            // Otherwise, remove all separators
            else -> digitsOnly.replace(Regex("""[.,]"""), "")
        }
    }

    private fun parseNumericValue(cleanValue: String): Long {
        return try {
            if (cleanValue.isBlank()) 0L else cleanValue.toLong()
        } catch (e: NumberFormatException) {
            Log.w(TAG, "Failed to parse numeric value: '$cleanValue'")
            0L
        }
    }

    private fun isValidCurrencyAmount(amount: Long): Boolean {
        return amount in MIN_CURRENCY_AMOUNT..MAX_CURRENCY_AMOUNT
    }

    private fun calculateConfidence(original: String, numericValue: Long): Double {
        var confidence = 0.3 // Lower base confidence

        // Boost confidence for explicit currency indicators
        when {
            original.contains("Rp", ignoreCase = true) -> confidence += 0.5
            original.contains("IDR", ignoreCase = true) -> confidence += 0.4
            original.contains("rupiah", ignoreCase = true) -> confidence += 0.3
        }

        // Boost confidence for common Indonesian denominations
        when (numericValue) {
            in listOf(1000L, 2000L, 5000L, 10000L, 20000L, 50000L, 100000L, 200000L, 500000L) -> confidence += 0.3
            in 1000L..999999L -> confidence += 0.2 // Reasonable small range
            in 1000000L..99999999L -> confidence += 0.1 // Reasonable large range
        }

        // Reduce confidence for unlikely amounts
        when {
            numericValue > 1_000_000_000L -> confidence -= 0.3 // Very large amounts
            numericValue in 1L..999L -> confidence -= 0.4 // Too small
        }

        // Boost confidence for proper formatting patterns
        when {
            Regex("""\d{1,3}(.\d{3})+""").matches(original.replace(Regex("""[^\d.]"""), "")) -> confidence += 0.1
            original.length >= 7 -> confidence += 0.05 // Longer strings might be more reliable
        }

        return confidence.coerceIn(0.0, 1.0)
    }

    private fun selectBestCandidate(candidates: List<CurrencyCandidate>): CurrencyCandidate? {
        if (candidates.isEmpty()) return null

        // Sort by priority first (lower number = higher priority), then by confidence (higher is better)
        return candidates
            .sortedWith(
                compareBy<CurrencyCandidate> { it.priority }
                    .thenByDescending { it.confidence }
                    .thenByDescending { it.numeric } // Prefer larger amounts if all else equal
            )
            .also { sortedCandidates ->
                Log.d(TAG, "🔍 Candidate ranking:")
                sortedCandidates.forEachIndexed { index, candidate ->
                    Log.d(TAG, "  ${index + 1}. ${candidate.original} -> ${candidate.cleaned} (P:${candidate.priority}, C:${String.format("%.2f", candidate.confidence)})")
                }
            }
            .firstOrNull()
    }

    private fun formatCurrency(amount: Long): String {
        return "Rp ${String.format("%,d", amount).replace(",", ".")}"
    }

    /**
     * Reset the extractor state - useful when switching screens or restarting
     */
    fun reset() {
        lastProcessedResult = null
        lastProcessedTimestamp = 0L
        isProcessing = false
        Log.d(TAG, "🔄 Currency extractor state reset")
    }

    /**
     * Check if currently processing to avoid overlapping requests
     */
    fun isCurrentlyProcessing(): Boolean = isProcessing

    fun preprocessOcrText(text: String): String {
        return text
            .replace(Regex("(?<=Rp)l(?=\\d)"), "1")  // Replace 'l' with '1' after 'Rp' if it's followed by a digit
            .replace("Rpl", "Rp1")                  // Or simple direct patch if common
            .replace("I", "1")                      // Optional: fix other common OCR confusions
    }

}