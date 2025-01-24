package dhandev.android.composedemo.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dhandev.android.composedemo.ui.modifier.roundedBackground

@Composable
fun DemoTextInputComp(
    modifier: Modifier = Modifier,
    text: String,
    onTextChanged: (String)->Unit
) {
    Box(
        modifier = modifier.roundedBackground()
    ) {
        val commonModifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
        BasicTextField(
            value = text,
            onValueChange = { onTextChanged(it) },
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp
            ),
            modifier = commonModifier
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )
        AnimatedVisibility(
            visible = text.isEmpty(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = "Your text will saved from config changes",
                modifier = commonModifier
            )
        }
    }
}
