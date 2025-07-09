package edts.android.composedemo.screenshot

import android.content.Intent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object MediaProjectionPermissionHolder {
    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.NotGranted)
    val permissionState: StateFlow<PermissionState> = _permissionState.asStateFlow()

    val resultCode: Int?
        get() = (_permissionState.value as? PermissionState.Granted)?.resultCode

    val dataIntent: Intent?
        get() = (_permissionState.value as? PermissionState.Granted)?.dataIntent

    val isPermissionGranted: Boolean
        get() = _permissionState.value is PermissionState.Granted

    fun set(resultCode: Int, intent: Intent) {
        _permissionState.value = PermissionState.Granted(resultCode, intent)
    }

    fun clear() {
        _permissionState.value = PermissionState.NotGranted
    }

    sealed class PermissionState {
        data object NotGranted : PermissionState()
        data class Granted(val resultCode: Int, val dataIntent: Intent) : PermissionState()
    }
}

