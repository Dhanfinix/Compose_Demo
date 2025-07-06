package edts.android.composedemo.overlay

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import edts.android.composedemo.ui.theme.ComposeDemoTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OverlayService : LifecycleService(), ViewModelStoreOwner, SavedStateRegistryOwner {
    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView

    private lateinit var savedStateController: SavedStateRegistryController
    private val vmStore = ViewModelStore()

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate() {
        // 1. Create and attach the SavedStateRegistryController early
        savedStateController = SavedStateRegistryController.create(this)
        savedStateController.performAttach()
        savedStateController.performRestore(null)

        super.onCreate() // Safe to call now; lifecycle proceeds to CREATED

        // 2. Create and configure overlay
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }

        // 3. Prepare ComposeView with lifecycle context
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)

            setContent {
                ComposeDemoTheme {
                    var sheetVisible by remember { mutableStateOf(true) }
                    val scope = rememberCoroutineScope()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Transparent) // maintain transparency
                    ) {
                        // Dimmed background
                        if (sheetVisible) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .background(Color(0x99000000)) // semi-transparent black
                                    .clickable { stopSelf() }
                            )
                        }

                        // Bottom Sheet Content
                        AnimatedVisibility(
                            visible = sheetVisible,
                            enter = slideInVertically(initialOffsetY = { it }),
                            exit = slideOutVertically(targetOffsetY = { it }),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        ) {
                            Surface(
                                color = Color.White,
                                tonalElevation = 8.dp,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 200.dp, max = 400.dp)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "📦 Custom Bottom Sheet",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(Modifier.height(12.dp))
                                    Text("This layout dims the background without covering other apps.")
                                    Spacer(Modifier.height(24.dp))
                                    Button(onClick = {
                                        scope.launch {
                                            sheetVisible = false
                                            delay(100)
                                            stopSelf()
                                        }
                                    }) {
                                        Text("Close Overlay")
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }

        // 4. Add view to window
        windowManager.addView(composeView, params)
    }

    override fun onDestroy() {
        if (::composeView.isInitialized) {
            windowManager.removeView(composeView)
        }
        vmStore.clear()
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        return super.onBind(intent)
    }

    // Required implementations for ViewModel/SavedState support
    override val viewModelStore: ViewModelStore
        get() = vmStore

    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateController.savedStateRegistry
}
