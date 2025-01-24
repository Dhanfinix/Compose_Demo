package dhandev.android.composedemo.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dhandev.android.composedemo.ui.modifier.RoundedRow

/**
 * This is a stateful component, the state is managed by the component itself.
 * Make it difficult to test or preview the component and other components
 * can't interact with it's value
 */
@Composable
fun CounterComp(
    modifier: Modifier = Modifier
) {
    var count by remember { mutableIntStateOf(0) }

    RoundedRow(
        modifier.fillMaxWidth()
    ){
        Text(
            text = "Count: $count",
            modifier = Modifier.weight(1f)
        )
        Button(onClick = { count++ }) {
            Text(text = "Increment")
        }
    }
}