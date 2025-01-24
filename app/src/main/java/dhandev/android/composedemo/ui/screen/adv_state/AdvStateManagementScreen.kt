package dhandev.android.composedemo.ui.screen.adv_state

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.NoteDialogComp

@Composable
fun AdvStateManagementScreen(
    modifier: Modifier = Modifier,
    viewModel: AdvStateManagementViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
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
                Text(
                    text = "No Notes",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    //todo: create note item with edit and delete button
                }
            }

            NoteDialogComp(
                isVisible = uiState.showInputDialog,
                onDismiss = { viewModel.showInputDialog(false) },
                onSave = { viewModel.addNote(it) }
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