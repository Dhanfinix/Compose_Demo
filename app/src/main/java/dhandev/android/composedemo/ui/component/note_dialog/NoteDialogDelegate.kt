package dhandev.android.composedemo.ui.component.note_dialog

interface NoteDialogDelegate {
    fun onSave(value: String){}
    fun onDismiss(){}
    fun onValueChange(value: String){}
}