package dhandev.android.composedemo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.ui.component.CounterComp
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.DemoTextInputComp
import dhandev.android.composedemo.ui.component.StateChangeComp

@Composable
fun StateManagementScreen(modifier: Modifier = Modifier) {
    DemoScaffoldComp(
        modifier = modifier,
        title = "State Management"
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                /** Using regular variable as state */
                var isActive = false
                StateChangeComp(
                    isActive,
                    title = "Regular variable"
                ){
                    isActive = !isActive
                }
            }
            item {
                /**
                 * Best Approach
                 * This is an example of State Hoisting in Compose.
                 *
                 * State Hoisting involves moving the state to a common ancestor
                 * of components that need to access and modify that state.
                 * Here, the `isActive` state is hoisted to the `StateManagementScreen`,
                 * allowing `StateChangeComp` to both read and modify it.
                 */
                var isActive by remember { mutableStateOf(false) }
                StateChangeComp(
                    isActive,
                    title = "Mutable State variable",
                ){
                    isActive = !isActive
                }
            }
            item {
                /** Stateful component */
                CounterComp()
            }
            /**
             * All Above example can't survive configuration change
             * like rotation/ theme change.
             * To make it survive we can use rememberSavable
             */
            item {
                var text by rememberSaveable { mutableStateOf("") }
                DemoTextInputComp(
                    text = text,
                    onTextChanged = {newText-> text = newText }
                )
            }
            // todo: 1. rememberSavable with data class
            // todo: 2. use viewModel as state management
        }
    }
}