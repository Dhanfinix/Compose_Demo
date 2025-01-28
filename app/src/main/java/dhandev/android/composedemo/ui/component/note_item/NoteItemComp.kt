package dhandev.android.composedemo.ui.component.note_item

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.ui.modifier.RoundedRow
import dhandev.android.composedemo.ui.modifier.conditional
import dhandev.android.composedemo.ui.modifier.roundedBackground
import dhandev.android.composedemo.ui.theme.ComposeDemoTheme

@Composable
fun NoteItemComp(
    modifier: Modifier = Modifier,
    noteItemState: NoteItemState,
    delegate: NoteItemDelegate
) {
    var lineCount by remember { mutableIntStateOf(0) }
    val isSingleLine by remember { derivedStateOf { lineCount == 1 } }
    val minHeight = 48.dp
    RoundedRow(
        modifier = modifier.fillMaxWidth(),
        vertcalAlignment = if(isSingleLine)
            Alignment.CenterVertically
        else
            Alignment.Top
    ) {
        Text(
            text = noteItemState.text,
            style = MaterialTheme.typography.bodyLarge,
            onTextLayout = {
                lineCount = it.lineCount
            },
            modifier = Modifier
                .roundedBackground(MaterialTheme.colorScheme.background)
                .weight(1f)
                .conditional(isSingleLine,
                    ifTrue = {
                        requiredHeightIn(min = 0.dp)
                    },
                    ifFalse = {
                        requiredHeightIn(min = minHeight)
                    }
                )
        )
        IconButton(
            onClick = { delegate.onEdit() }
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Note"
            )
        }
        IconButton(
            onClick = { delegate.onDelete() }
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Note",
                tint = Color.Red
            )
        }
    }
}

@Preview
@Composable
private fun NoteItemPreview(
    @PreviewParameter(ProvideNoteItemPreview::class) text: String
) {
    ComposeDemoTheme {
        NoteItemComp(
            noteItemState = NoteItemState(text),
            delegate = object : NoteItemDelegate {}
        )
    }
}

private class ProvideNoteItemPreview : PreviewParameterProvider<String>{
    override val values = sequenceOf(
        "This is short note",
        "This is longer note that is very long, it need multiple line to load on component, wait you need more text? here we go."
    )
}