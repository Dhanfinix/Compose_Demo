package edts.android.composedemo.ui.screen.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.Gravity
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.ui.platform.ComposeView
import androidx.core.app.NotificationCompat
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
            WindowManager.LayoutParams.MATCH_PARENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            // For edge-to-edge, ensure we cover system bars
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
            }
        }

        val notification = createOverlayNotification()
        startForeground(NOTIFICATION_ID, notification)

        // 3. Prepare ComposeView with lifecycle context
        composeView = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@OverlayService)
            setViewTreeViewModelStoreOwner(this@OverlayService)
            setViewTreeSavedStateRegistryOwner(this@OverlayService)

            // Enable edge-to-edge for the ComposeView
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                setOnApplyWindowInsetsListener { view, insets ->
                    view.setPadding(0, 0, 0, 0)
                    insets
                }
            }

            setContent {
                OverlayServiceScreen(
                    delegate = object : OverlayServiceDelegate {
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

    private fun createOverlayNotification(): Notification {
        val channelId = "overlay_channel"
        val channelName = "Overlay Notifications"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Overlay Active")
            .setContentText("Tap to return to app")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    companion object{
        const val OVERLAY_STOP_INTENT = "edts.android.ACTION_OVERLAY_STOPPED"
        const val NOTIFICATION_ID = 101
    }
}
