package edts.android.composedemo.utils

import android.accessibilityservice.AccessibilityService
import android.app.Activity
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
import edts.android.composedemo.app_monitor.AppMonitorService
import edts.android.composedemo.constants.ThemeMode

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
    ) = isAccessibilityServiceEnabled(context, AppMonitorService::class.java)

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


}
