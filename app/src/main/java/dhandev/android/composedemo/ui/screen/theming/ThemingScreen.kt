package dhandev.android.composedemo.ui.screen.theming

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.LocalTheme
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.segment_button.SegmentedButtonComp
import dhandev.android.composedemo.ui.theme.ColorFFF
import dhandev.android.composedemo.ui.theme.ColorRed100
import dhandev.android.composedemo.ui.theme.MontserratFamily
import dhandev.android.composedemo.ui.theme.body1
import dhandev.android.composedemo.ui.theme.headline3

@Composable
fun ThemingScreen(
    modifier: Modifier = Modifier,
    viewModel: ThemingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentTheme = LocalTheme.current
    viewModel.setSelectedTheme(currentTheme)

    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.Theming().title
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SegmentedButtonComp(
                    uiState = uiState.themeOptions
                ) {_, themeMode->
                    if (themeMode != null) {
                        viewModel.changeTheme(themeMode)
                    }
                }
            }
            item {
                Button(onClick = {}) {
                    Text("Themed Button")
                }
            }
            item {
                Text("Above button styling is following theme, if you don't want it, set the color manually")
                Button(
                    onClick = {},
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = ColorRed100,
                        contentColor = ColorFFF
                    )
                ) {
                    Text("Manually Set Button")
                }
            }
            item {
                Text(
                    text = "This text is written in Montserrat family with headline3 style",
                    style = MontserratFamily.headline3()
                )
            }
            item {
                Text(
                    text = "This text is written in Montserrat family with body1 style",
                    style = MontserratFamily.body1()
                )
            }
        }
    }
}