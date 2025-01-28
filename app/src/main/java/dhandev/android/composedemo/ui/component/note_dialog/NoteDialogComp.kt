package dhandev.android.composedemo.ui.component.note_dialog

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import dhandev.android.composedemo.ui.theme.ComposeDemoTheme

@Composable
fun NoteDialogComp(
    modifier: Modifier = Modifier,
    uiState: NoteDialogState,
    delegate: NoteDialogDelegate,
) {
    val focusRequester = remember { FocusRequester() }

    val doSave = {
        delegate.onSave(uiState.note)
        delegate.onDismiss()
    }

    AnimatedVisibility(uiState.isVisible) {
        LaunchedEffect(Unit) {
            focusRequester.requestFocus()
        }
        Dialog(
            onDismissRequest = { delegate.onDismiss() },
        ) {
            Card {
                Column(
                    modifier = modifier.padding(16.dp),
                ) {
                    Text(
                        text = "${if (uiState.isEdit) "Edit" else "Add"} Note",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    OutlinedTextField(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .focusRequester(focusRequester)
                                .padding(top = 16.dp, bottom = 32.dp),
                        value = uiState.note,
                        keyboardOptions =
                            KeyboardOptions.Default.copy(
                                imeAction = ImeAction.Done,
                            ),
                        keyboardActions =
                            KeyboardActions(
                                onDone = { doSave() },
                            ),
                        singleLine = true,
                        placeholder = {
                            Text("Write your note here...")
                        },
                        onValueChange = { delegate.onValueChange(it) },
                    )

                    Row(
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        TextButton(
                            onClick = { delegate.onDismiss() },
                        ) {
                            Text("Discard")
                        }
                        TextButton(
                            onClick = { doSave() },
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DialogPreview() {
    ComposeDemoTheme {
        NoteDialogComp(
            uiState = NoteDialogState(),
            delegate = object : NoteDialogDelegate{}
        )
    }
}
