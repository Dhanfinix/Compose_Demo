package edts.android.composedemo.app_monitor

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import edts.android.composedemo.overlay.OverlayService

class AppMonitorService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        val packageName = event?.packageName?.toString()
        if (packageName in listOf("com.gojek.gopay", "ovo.id", "mypoin.indomaret.android")) {
            val intent = Intent(this, OverlayService::class.java)
            startService(intent)
        }
    }

    override fun onInterrupt() {}
}