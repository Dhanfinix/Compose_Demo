package edts.android.composedemo.ui.component

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle

@Composable
fun OnlineStatus(isOnline: Boolean) {
    val annotatedString = buildAnnotatedString {
        append("Is Online: ")
        withStyle(
            style = SpanStyle(
                background = if (isOnline) Color.Green else Color.Red,
                color = Color.White // Set text color to white for better contrast
            )
        ) {
            append("$isOnline")
        }
    }

    Text(
        text = annotatedString
    )
}
