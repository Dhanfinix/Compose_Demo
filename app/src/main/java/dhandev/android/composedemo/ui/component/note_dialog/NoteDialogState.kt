package dhandev.android.composedemo.ui.component.note_dialog

data class NoteDialogState(
    val note: String = "",
    val isVisible: Boolean = false,
    val index: Int? = null,
){
    val isEdit = index != null
}