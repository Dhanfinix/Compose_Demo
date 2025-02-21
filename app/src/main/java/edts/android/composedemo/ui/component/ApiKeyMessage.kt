package edts.android.composedemo.ui.component

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp

/**
 * AI Generated Component, displays a message indicating that no API key has been found.
 */
@Composable
fun ApiKeyMessage() {
    val context = LocalContext.current
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "No API Key Found", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Please add your own Finlight News API key")
        Text(text = "You can obtain the API key from ")
        Text(
            text = "Finlight News API Documentation",
            color = Color.Blue,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://app.finlight.me/"))
                context.startActivity(intent)
            }
        )
        Text(text = "Once you have the API key, add it to your local.properties file as follows:")
        Text(
            text = "API_KEY = your_api_key",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}
