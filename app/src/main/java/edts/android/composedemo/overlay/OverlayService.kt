package edts.android.composedemo.overlay

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner

class OverlayService : LifecycleService(), ViewModelStoreOwner, SavedStateRegistryOwner {
    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView

    private lateinit var savedStateController: SavedStateRegistryController
    private val vmStore = ViewModelStore()

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
                OverlayServiceScreen(
                    delegate = object : OverlayServiceDelegate{
                        override fun doStopSelf() {
                            stopSelf()
                        }
                    }
                )
            }
        }

        // 4. Add view to window
        windowManager.addView(composeView, params)
    }

    override fun onDestroy() {
        // Notify UI that we're closing
        val intent = Intent(OVERLAY_STOP_INTENT).apply {
            `package` = packageName // limit broadcast to this app
        }
        sendBroadcast(intent)

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

    companion object{
        const val OVERLAY_STOP_INTENT = "edts.android.ACTION_OVERLAY_STOPPED"
    }
}
