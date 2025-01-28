package dhandev.android.composedemo.ui.screen.adv_state

import androidx.lifecycle.ViewModel
import dhandev.android.composedemo.ui.component.note_item.NoteItemState
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
                notes = it.notes?.toMutableList()?.apply {
                    add(NoteItemState(note))
                } ?: listOf(NoteItemState(note))
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
            val updated = it.notes?.get(index)?.copy(text = note) ?: NoteItemState(note)
            it.copy(
                notes = it.notes?.toMutableList()?.apply {
                    set(index, updated)
                }
            )
        }
    }
    fun showInputDialog(visible: Boolean = true, index: Int? = null, value: String = "") {
        _uiState.update {
            it.copy(
                noteDialog = it.noteDialog.copy(
                    isVisible = visible,
                    index = index,
                    note = value
                )
            )
        }
        if (!visible) onDialogValueChange("")
    }
    fun onDialogValueChange(value: String){
        _uiState.update {
            it.copy(
                noteDialog = it.noteDialog.copy(
                    note = value
                )
            )
        }
    }
}