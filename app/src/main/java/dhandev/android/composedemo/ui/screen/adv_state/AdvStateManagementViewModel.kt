package dhandev.android.composedemo.ui.screen.adv_state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AdvStateManagementViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdvScreenState())
    val uiState : StateFlow<AdvScreenState>
        get() = _uiState

    fun addNote(note: String) {
        _uiState.update {
            it.copy(
                notes = it.notes?.toMutableList()?.apply { add(note) }
            )
        }
    }
    fun removeNote(index: Int) {
        _uiState.update {
            it.copy(
                notes = it.notes?.toMutableList()?.apply { removeAt(index) }
            )
        }
    }
    fun changeNote(index: Int, note: String) {
        _uiState.update {
            it.copy(
                notes = it.notes?.toMutableList()?.apply { set(index, note) }
            )
        }
    }
    fun showInputDialog(visible: Boolean) {
        _uiState.update {
            it.copy(
                showInputDialog = visible
            )
        }
    }
}