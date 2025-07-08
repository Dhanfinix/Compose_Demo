package edts.android.composedemo.app_monitor

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AppMonitorStatus {
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    fun markRunning() { _isRunning.value = true }
    fun markStopped() { _isRunning.value = false }
}
