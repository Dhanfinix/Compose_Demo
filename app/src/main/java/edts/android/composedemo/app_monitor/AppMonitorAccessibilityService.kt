package edts.android.composedemo.app_monitor

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import edts.android.composedemo.ui.screen.overlay.OverlayService

/**
 * Monitors app transitions to trigger an overlay when a target app is freshly opened.
 */
class AppMonitorAccessibilityService : AccessibilityService() {

    private val targetPackages = setOf(
        "com.gojek.gopay",
        "ovo.id",
        "mypoin.indomaret.android"
    )

    private var currentTargetPackage: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Only respond to new window state events
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val pkg = event.packageName?.toString() ?: return
        val isTarget = pkg in targetPackages
        val isNotThisApp = pkg != packageName.toString() // to prevent close directly after shows
        val isFirstOpen = event.contentChangeTypes == AccessibilityEvent.CONTENT_CHANGE_TYPE_UNDEFINED

        when {
            // Entered a target package for the first time → show overlay
            isTarget && isFirstOpen -> {
                currentTargetPackage = pkg
                startOverlay()
            }
            // Exited target package → stop overlay
            !isTarget && isNotThisApp && currentTargetPackage != null -> {
                currentTargetPackage = null
                stopOverlay()
            }
        }
    }

    private fun startOverlay() {
        startService(Intent(applicationContext, OverlayService::class.java))
    }

    private fun stopOverlay() {
        stopService(Intent(applicationContext, OverlayService::class.java))
    }

    override fun onServiceConnected() {
        currentTargetPackage = null
    }

    override fun onInterrupt() {}
}
