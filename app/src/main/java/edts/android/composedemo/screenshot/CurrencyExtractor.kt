package edts.android.composedemo.screenshot

import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions

class CurrencyExtractor {

    companion object {
        private const val TAG = "CurrencyExtractor"
        private const val TARGET_WIDTH = 720
        private const val MIN_CURRENCY_AMOUNT = 1_000L
        private const val MAX_CURRENCY_AMOUNT = 10_000_000_000L
    }

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    private data class CurrencyCandidate(
        val original: String,
        val cleaned: String,
        val numeric: Long,
        val priority: Int,
        val confidence: Double
    )

    fun extractFromBitmap(bitmap: Bitmap, onSuccess: (String) -> Unit, onFailure: (String) -> Unit, onRejected: (String, String) -> Unit) {
        val resized = bitmap.scale(TARGET_WIDTH, (bitmap.height * TARGET_WIDTH.toFloat() / bitmap.width).toInt())
        val inputImage = InputImage.fromBitmap(resized, 0)

        recognizer.process(inputImage)
            .addOnSuccessListener { visionText ->
                val extractedNominal = extractCurrencyNominal(visionText.text, onRejected)

                extractedNominal?.let {
                    Log.d(TAG, "💰 Currency nominal extracted: $it")
                    onSuccess(it)
                } ?: run {
                    Log.d(TAG, "💸 No valid currency nominal found.")
                    onFailure("No valid currency nominal found")
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "OCR Failed: ${e.message}")
                onFailure("OCR Failed: ${e.message}")
            }
    }

    private fun extractCurrencyNominal(text: String, onRejected: (String, String) -> Unit): String? {
        val patterns = listOf(
            // Rp 50.000 or Rp50.000 or Rp 50,000
            Regex("""(?:Rp\.?\s*)([\d.,]+)""", RegexOption.IGNORE_CASE),
            // IDR 50000 or IDR 50.000
            Regex("""(?:IDR\s*)([\d.,]+)""", RegexOption.IGNORE_CASE),
            // 50.000 (standalone numbers with separators, min 4 digits)
            Regex("""(?<!\d)([\d]{1,3}(?:[.,]\d{3})+)(?!\d)"""),
            // 50000 (standalone numbers without separators, min 4 digits)
            Regex("""(?<!\d)(\d{4,})(?!\d)""")
        )

        val candidates = mutableListOf<CurrencyCandidate>()

        patterns.forEachIndexed { priority, pattern ->
            pattern.findAll(text).forEach { match ->
                val rawValue = match.groupValues[1]
                val cleanValue = cleanCurrencyValue(rawValue)
                val numericValue = parseNumericValue(cleanValue)

                if (isValidCurrencyAmount(numericValue)) {
                    candidates.add(
                        CurrencyCandidate(
                            original = match.value,
                            cleaned = cleanValue,
                            numeric = numericValue,
                            priority = priority,
                            confidence = calculateConfidence(match.value, numericValue)
                        )
                    )
                } else {
                    // Callback for rejected values
                    val reason = when {
                        numericValue < MIN_CURRENCY_AMOUNT -> "Below minimum amount ($MIN_CURRENCY_AMOUNT)"
                        numericValue > MAX_CURRENCY_AMOUNT -> "Above maximum amount ($MAX_CURRENCY_AMOUNT)"
                        numericValue == 0L -> "Invalid number format"
                        else -> "Invalid amount"
                    }
                    onRejected(match.value, reason)
                    Log.d(TAG, "🚫 Rejected: ${match.value} - $reason")
                }
            }
        }

        return selectBestCandidate(candidates)?.cleaned
    }

    private fun cleanCurrencyValue(value: String): String {
        // Remove all non-digit characters except dots and commas
        val digitsOnly = value.replace(Regex("""[^\d.,]"""), "")

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
            // If dot with exactly 3 digits after, might be thousand separator
            Regex("""\d+\.\d{3}$""").matches(digitsOnly) -> {
                digitsOnly.replace(".", "")
            }
            // Otherwise, remove all separators
            else -> digitsOnly.replace(Regex("""[.,]"""), "")
        }
    }

    private fun parseNumericValue(cleanValue: String): Long {
        return try {
            cleanValue.toLong()
        } catch (e: NumberFormatException) {
            0L
        }
    }

    private fun isValidCurrencyAmount(amount: Long): Boolean {
        // Indonesian Rupiah typically ranges from 1,000 to 10,000,000,000
        return amount in MIN_CURRENCY_AMOUNT..MAX_CURRENCY_AMOUNT
    }

    private fun calculateConfidence(original: String, numericValue: Long): Double {
        var confidence = 0.5 // Base confidence

        // Boost confidence for explicit currency indicators
        when {
            original.contains("Rp", ignoreCase = true) -> confidence += 0.4
            original.contains("IDR", ignoreCase = true) -> confidence += 0.3
        }

        // Boost confidence for common Indonesian denominations
        when (numericValue) {
            in listOf(1000L, 2000L, 5000L, 10000L, 20000L, 50000L, 100000L) -> confidence += 0.2
            in MIN_CURRENCY_AMOUNT..999999L -> confidence += 0.1 // Reasonable range
        }

        // Reduce confidence for unlikely amounts
        if (numericValue > 100_000_000L) confidence -= 0.2

        return confidence.coerceIn(0.0, 1.0)
    }

    private fun selectBestCandidate(candidates: List<CurrencyCandidate>): CurrencyCandidate? {
        if (candidates.isEmpty()) return null

        // Sort by priority first (lower is better), then by confidence (higher is better)
        return candidates
            .sortedWith(compareBy<CurrencyCandidate> { it.priority }.thenByDescending { it.confidence })
            .firstOrNull()
    }
}