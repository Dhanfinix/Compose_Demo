package edts.android.composedemo.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import edts.android.composedemo.app_monitor.AppMonitorAccessibilityService
import edts.android.composedemo.constants.ThemeMode
import android.os.Process
import android.view.View
import android.view.WindowManager
import androidx.core.app.AppOpsManagerCompat

object AndroidUtil {
    @Composable
    fun UpdateSystemBars(themeMode: ThemeMode) {
        val context = LocalContext.current
        val activity = context as? Activity
        val window = activity?.window ?: return
        val view = LocalView.current

        val useDarkIcons =
            themeMode == ThemeMode.LIGHT || themeMode == ThemeMode.CUSTOM ||
                (themeMode == ThemeMode.SYSTEM && !isSystemInDarkTheme())

        val windowInsetsController = WindowCompat.getInsetsController(window, view)
        windowInsetsController.isAppearanceLightStatusBars = useDarkIcons
        windowInsetsController.isAppearanceLightNavigationBars = useDarkIcons
    }

    fun getAccessibilityEnabled(
        context: Context
    ) = isAccessibilityServiceEnabled(context, AppMonitorAccessibilityService::class.java)

    private fun isAccessibilityServiceEnabled(
        context: Context,
        serviceClass: Class<out AccessibilityService>
    ): Boolean {
        val expectedComponent = ComponentName(context, serviceClass)
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val accessibilityEnabled = Settings.Secure.getInt(
            context.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED, 0
        ) == 1

        if (!accessibilityEnabled || enabledServicesSetting.isNullOrEmpty()) return false

        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServicesSetting)

        for (componentName in colonSplitter) {
            if (ComponentName.unflattenFromString(componentName) == expectedComponent) {
                return true
            }
        }
        return false
    }

    fun getAutostartIntent(context: Context): Intent? {
        val manufacturer = Build.MANUFACTURER.lowercase()
        return when {
            manufacturer.contains("xiaomi") -> Intent().apply {
                component = ComponentName(
                    "com.miui.securitycenter",
                    "com.miui.permcenter.autostart.AutoStartManagementActivity"
                )
            }
            manufacturer.contains("oppo") -> Intent().apply {
                component = ComponentName(
                    "com.coloros.safecenter",
                    "com.coloros.safecenter.startup.StartupAppListActivity"
                )
            }
            manufacturer.contains("vivo") -> Intent().apply {
                component = ComponentName(
                    "com.vivo.permissionmanager",
                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"
                )
            }
            else -> null // Unsupported or stock Android
        }
    }

    fun checkUsageAccessPermission(context: Context): Boolean {
        val mode = AppOpsManagerCompat.noteOp(
            context,
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun getForegroundApp(context: Context): String? {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val time = System.currentTimeMillis()

        val usageEvents = usageStatsManager.queryEvents(time - 2000, time)
        var lastPackage: String? = null
        val event = UsageEvents.Event()
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                lastPackage = event.packageName
            }
        }
        return lastPackage
    }

    fun getForegroundAppHybrid(context: Context): String? {
        val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val endTime = System.currentTimeMillis()
        val beginTime = endTime - 5000 // ambil 5 detik terakhir

        var lastEventTime = 0L
        var currentApp: String? = null

        // Pertama: telusuri UsageEvents terbaru
        val events = usm.queryEvents(beginTime, endTime)
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                event.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
            ) {
                if (event.timeStamp > lastEventTime) {
                    currentApp = event.packageName
                    lastEventTime = event.timeStamp
                }
            }
        }

        // Kedua: fallback ke snapshot "most recent foreground"
        if (currentApp == null) {
            val usageStats = usm.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY,
                beginTime,
                endTime
            )

            if (!usageStats.isNullOrEmpty()) {
                currentApp = usageStats
                    .filter { it.lastTimeUsed > 0 }
                    .maxByOrNull { it.lastTimeUsed }
                    ?.packageName
            }
        }

        return currentApp
    }

    fun View.setSecureFlag() {
        this.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.rootView?.rootView?.apply {
                    val lp = this@apply.layoutParams as? WindowManager.LayoutParams
                    lp?.let {
                        lp.flags = lp.flags.or(WindowManager.LayoutParams.FLAG_SECURE)
                    }
                    val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    wm.updateViewLayout(this, lp)
                }
                v.removeOnAttachStateChangeListener(this)
            }

            override fun onViewDetachedFromWindow(v: View) {}
        })
    }

}
