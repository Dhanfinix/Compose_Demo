package edts.android.composedemo.ui.screen.adv_state

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.ui.component.note_dialog.NoteDialogComp
import edts.android.composedemo.ui.component.note_dialog.NoteDialogDelegate
import edts.android.composedemo.ui.component.note_item.NoteItemComp
import edts.android.composedemo.ui.component.note_item.NoteItemDelegate
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

@Composable
fun AdvStateManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: AdvStateManagementViewModel = viewModel(),
    deeplinkData: String? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    val uiStateLifecycle by viewModel.uiState.collectAsStateWithLifecycle()
    deeplinkData?.let {
        Toast.makeText(LocalContext.current, it, Toast.LENGTH_SHORT).show()
    }
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.AdvStateManagement().title,
        fab = { AddNoteFab(onClick = {
            viewModel.showInputDialog(true)
        }) }
    ) {innerPadding->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (uiState.notes.isNullOrEmpty()) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No Notes",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                    //this to demo collectLatestWithLifeCycle,
                    //in short it stop collecting latest data from viewModel when
                    //app paused and resume when back to foreground
                    Text(
                        text = "Active for ${uiStateLifecycle.stepCount} Second",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Log.d("Collecting Latest StepCount...", uiStateLifecycle.stepCount.toString())
                }
            } else {
                AnimatedContent(
                    targetState = uiState.notes,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    LazyColumn(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(it!!){index, note->
                            NoteItemComp(
                                noteItemState = note,
                                delegate = object : NoteItemDelegate{
                                    override fun onDelete() {
                                        viewModel.removeNote(index)
                                    }
                                    override fun onEdit() {
                                        viewModel.showInputDialog(true, index, note.text)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            NoteDialogComp(
                uiState = uiState.noteDialog,
                delegate = object : NoteDialogDelegate {
                    override fun onDismiss() {
                        viewModel.showInputDialog(false)
                    }

                    override fun onSave(value: String) {
                        if (uiState.noteDialog.index != null)
                            viewModel.changeNote(uiState.noteDialog.index!!, value)
                        else
                            viewModel.addNote(value)
                    }

                    override fun onValueChange(value: String) {
                        viewModel.onDialogValueChange(value)
                    }
                }
            )
        }
    }
}

@Composable
private fun AddNoteFab(onClick: ()->Unit){
    FloatingActionButton(onClick) {
        Icon(Icons.Default.Add, null)
    }
}

@Preview
@Composable
private fun AdvStateManPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = {AdvStateManagementScreen()}
    )
}