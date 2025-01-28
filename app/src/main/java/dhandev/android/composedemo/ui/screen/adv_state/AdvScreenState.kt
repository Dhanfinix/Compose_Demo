package dhandev.android.composedemo.ui.screen.adv_state

import dhandev.android.composedemo.ui.component.note_dialog.NoteDialogState
import dhandev.android.composedemo.ui.component.note_item.NoteItemState

data class AdvScreenState(
    val notes: List<NoteItemState>? = null,
    // text input state
    val noteDialog: NoteDialogState = NoteDialogState()
)
