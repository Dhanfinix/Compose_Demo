package edts.android.composedemo.ui.screen.adv_state

import edts.android.composedemo.ui.component.note_dialog.NoteDialogState
import edts.android.composedemo.ui.component.note_item.NoteItemState

data class AdvScreenState(
    val notes: List<NoteItemState>? = null,
    // text input state
    val noteDialog: NoteDialogState = NoteDialogState(),
    val stepCount: Int = 0
)
