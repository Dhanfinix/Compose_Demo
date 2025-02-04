package edts.android.composedemo.ui.component

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import edts.android.composedemo.ui.modifier.RoundedRow

/**
 * This component is stateless, the state is obtained from parameters
 * and any action is handled by callback. This is good because it can
 * make the code more reusable and easier to test or previewed
 */
@Composable
fun StateChangeComp(
    isActive: Boolean,
    title: String,
    onChanged: () -> Unit
) {
    val context = LocalContext.current
    RoundedRow(Modifier.fillMaxWidth()) {
        Column(Modifier.weight(1f)) {
            Text(title)
            Text("Current Button State = $isActive")
            Button(
                onClick = {
                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show()
                },
                enabled = isActive
            ) {
                Text("Click Me!")
            }
        }
        Switch(
            checked = isActive,
            onCheckedChange = { onChanged() },
            modifier = Modifier.align(Alignment.CenterVertically)
        )
    }
}