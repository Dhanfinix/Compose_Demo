package dhandev.android.composedemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import dhandev.android.composedemo.constants.LocalActivity
import dhandev.android.composedemo.constants.LocalTheme
import dhandev.android.composedemo.constants.ThemeMode
import dhandev.android.composedemo.ui.theme.ComposeDemoTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel = hiltViewModel<MainViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            CompositionLocalProvider(
                LocalActivity provides this,
                LocalTheme provides state.theme
            ) {
                ComposeDemoTheme(
                    mode = LocalTheme.current
                ) {
                    NavigationHost()
                }
            }
        }
    }
}