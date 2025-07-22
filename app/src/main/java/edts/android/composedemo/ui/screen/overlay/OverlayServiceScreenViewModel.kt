package edts.android.composedemo.ui.screen.overlay

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class UiState(
    val nominal: String = "0",
    val reason: String = ""
)
@HiltViewModel
class OverlayServiceScreenViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState : StateFlow<UiState> = _uiState

    fun updateNominal(newValue: String) = _uiState.update {
        it.copy(
            nominal = newValue
        )
    }
    fun updateReason(newValue: String) = _uiState.update {
        it.copy(
            reason = newValue
        )
    }

}