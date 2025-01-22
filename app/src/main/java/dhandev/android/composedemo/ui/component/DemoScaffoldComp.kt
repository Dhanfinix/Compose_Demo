package dhandev.android.composedemo.ui.component

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dhandev.android.composedemo.constants.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScaffoldComp(
    modifier: Modifier = Modifier,
    title: String,
    isLargeTopAppBar: Boolean = false,
    enableBackNavigation: Boolean = !isLargeTopAppBar,
    content: @Composable (PaddingValues)->Unit
) {
    val navController = LocalNavController.current
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (isLargeTopAppBar){
                LargeTopAppBar(
                    title = { Text(title) }
                )
            } else {
                MediumTopAppBar(
                    title = { Text(title) },
                    navigationIcon = {
                        if (enableBackNavigation) {
                            IconButton(
                                onClick = { navController.navigateUp() }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = null
                                )
                            }
                        }
                    }
                )
            }
        }
    ){innerPadding ->
        content(innerPadding)
    }
}