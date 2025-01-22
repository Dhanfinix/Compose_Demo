package dhandev.android.composedemo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.ui.component.HomeButtonComp
import dhandev.android.composedemo.ui.theme.ComposeDemoTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val destinations = listOf(Destinations.SimpleComponent(), Destinations.BasicLayout())
    val context = LocalContext.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            LargeTopAppBar(
                title = { Text("Compose Demo") }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            items(destinations){
                HomeButtonComp(text = it.title) {
                    Toast.makeText(context, "navigate to ${it.title} screen", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Preview
@Composable
private fun HomePreview() {
    ComposeDemoTheme {
        HomeScreen()
    }
}