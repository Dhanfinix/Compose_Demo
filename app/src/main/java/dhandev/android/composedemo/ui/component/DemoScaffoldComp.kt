package dhandev.android.composedemo.ui.component

import androidx.activity.compose.BackHandler
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
import androidx.compose.material3.TopAppBarDefaults.exitUntilCollapsedScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import dhandev.android.composedemo.constants.LocalNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScaffoldComp(
    modifier: Modifier = Modifier,
    title: String,
    isLargeTopAppBar: Boolean = false,
    enableBackNavigation: Boolean = !isLargeTopAppBar,
    fab: @Composable ()->Unit = {},
    content: @Composable (PaddingValues)->Unit
) {
    val navController = LocalNavController.current
    val scrollBehavior = exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    // same as coordinator layout in view-based UI, it contain slot of view that can be used
    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            if (isLargeTopAppBar){
                LargeTopAppBar(
                    title = { Text(title) },
                    scrollBehavior = scrollBehavior
                )
            } else {
                MediumTopAppBar(
                    title = { Text(title) },
                    scrollBehavior = scrollBehavior,
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
        },
        floatingActionButton = fab
    ){innerPadding ->
        content(innerPadding)
    }

    BackHandler {
        if (enableBackNavigation){
            navController.navigateUp()
        }
    }
}