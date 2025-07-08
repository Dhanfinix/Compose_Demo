package edts.android.composedemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import edts.android.composedemo.constants.LocalActivity
import edts.android.composedemo.constants.LocalTheme
import edts.android.composedemo.ui.theme.ComposeDemoTheme

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
        requestNotifPermission()
    }

    private fun requestNotifPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1001 // Your request code
                )
            }
        }
    }
}