package dhandev.android.composedemo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.data.LoginData
import dhandev.android.composedemo.data.LoginDataKotlin
import dhandev.android.composedemo.data.loginDataSaver
import dhandev.android.composedemo.ui.component.CounterComp
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.DemoTextInputComp
import dhandev.android.composedemo.ui.component.StateChangeComp
import dhandev.android.composedemo.ui.modifier.RoundedColumn

@Composable
fun StateManagementScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.StateManagement().title
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
            item {
                HorizontalDivider()
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
                    hint = "Your text will saved from config changes",
                    onTextChanged = {newText-> text = newText }
                )
            }
            /** * Login Data with rememberSavable
             * Using `rememberSavable` to remember the state of a data class (`LoginData`).
             * This ensures that the login data (username and password) survives configuration
             * changes.
             * Make sure your data class is extend Serializable from java.io
             * or @Serializable from kotlin, but you need saver instance.
             * */
            item {
                var loginData by rememberSaveable {
                    mutableStateOf(LoginData())
                }
                /** Uncomment to see @Serializable in action
                 * FYI, the result is same */
//                var loginData by rememberSaveable(stateSaver = loginDataSaver) {
//                    mutableStateOf(LoginDataKotlin())
//                }
                RoundedColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Demo of remember data class")
                    DemoTextInputComp(
                        text = loginData.username,
                        hint = "Username",
                        bgColor = MaterialTheme.colorScheme.surfaceBright,
                        onTextChanged = {newText->
                            loginData = loginData.copy(username = newText)
                        }
                    )
                    DemoTextInputComp(
                        text = loginData.password,
                        hint = "Password",
                        bgColor = MaterialTheme.colorScheme.surfaceBright,
                        onTextChanged = {newText->
                            loginData = loginData.copy(password = newText)
                        }
                    )
                    Button(
                        onClick = {
                            Toast.makeText(context, loginData.toString(), Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Submit")
                    }
                }
            }
            // todo: 2. use viewModel as state management
        }
    }
}