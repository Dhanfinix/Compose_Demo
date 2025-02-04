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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.LocalNavController
import dhandev.android.composedemo.constants.ThemeMode
import dhandev.android.composedemo.data.LoginData
import dhandev.android.composedemo.ui.component.CounterComp
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.DemoTextInputComp
import dhandev.android.composedemo.ui.component.StateChangeComp
import dhandev.android.composedemo.ui.modifier.RoundedColumn
import dhandev.android.composedemo.utils.preview.PreviewWrapperComp
import dhandev.android.composedemo.utils.preview.ThemePreviewProvider

@Composable
fun StateManagementScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var isActiveState by remember { mutableStateOf(false) }

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
            //No.1
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
            //No.2
            item {
                /**
                 * This is an example of State Hoisting in Compose.
                 *
                 * State Hoisting involves moving the state to a common ancestor
                 * of components that need to access and modify that state.
                 * Here, the `isActive` state is hoisted to the `StateManagementScreen`,
                 * allowing `StateChangeComp` to both read and modify it.
                 */
                StateChangeComp(
                    isActiveState,
                    title = "Mutable State variable",
                ){
                    isActiveState = !isActiveState
                }
            }
            //No.3
            if (isActiveState){
                item {
                    Text("This text appear only when state is active")
                }
            }
            //No.4
            item {
                /** Stateful component */
                CounterComp()
            }
            //No.5
            item {
                var text by remember { mutableStateOf("") }
                DemoTextInputComp(
                    text = text,
                    hint = "Your text won't be saved from config changes",
                    onTextChanged = {newText-> text = newText }
                )
            }
            //No.6
            item {
                HorizontalDivider()
            }
            //No.7
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
            //No.8
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
            //No.9
            item {
                val navController = LocalNavController.current
                Button(
                    onClick = { navController.navigate(Destinations.AdvStateManagement(data = "Testing"))  },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Advance State with View Model")
                }
            }
        }
    }
}

@Preview
@Composable
private fun StateManagementPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = { StateManagementScreen() }
    )
}