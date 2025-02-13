package edts.android.composedemo.ui.component

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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import edts.android.composedemo.constants.LocalNavController
import edts.android.composedemo.constants.LocalScreenName
import edts.android.composedemo.ui.component.side_effect.TrackLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DemoScaffoldComp(
    modifier: Modifier = Modifier,
    title: String,
    isLargeTopAppBar: Boolean = false,
    enableBackNavigation: Boolean = !isLargeTopAppBar,
    fab: @Composable ()->Unit = {},
    handleBack: (NavController)->Unit = {it.navigateUp()},
    content: @Composable (PaddingValues)->Unit
) {
    val navController = LocalNavController.current
    val scrollBehavior = exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    // Add lifecycle tracker
    CompositionLocalProvider(
        LocalScreenName provides title
    ) {
        TrackLifecycle()
    }

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
                                onClick = { handleBack(navController) }
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

    /** Handle back navigation from System Back Button */
    BackHandler(
        enabled = enableBackNavigation
    ) {
        handleBack(navController)
    }
}

