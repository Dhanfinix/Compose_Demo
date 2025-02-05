package edts.android.composedemo.ui.screen.theming

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.LocalTheme
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.ui.component.segment_button.SegmentedButtonComp
import edts.android.composedemo.ui.theme.ColorFFF
import edts.android.composedemo.ui.theme.ColorRed100
import edts.android.composedemo.ui.theme.MontserratFamily
import edts.android.composedemo.ui.theme.body1
import edts.android.composedemo.ui.theme.headline1
import edts.android.composedemo.ui.theme.headline3
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

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
                    text = "Headline1 Styled Text",
                    style = MontserratFamily.headline1()
                )
                //or manually set it
                Text(
                    text = "Text with primary color",
                    fontFamily = MontserratFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    lineHeight = 38.sp,
                    color = MaterialTheme.colorScheme.primary
                )
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
            item {
                Box(
                    modifier = Modifier
                        .size(width = 200.dp, height = 100.dp)
                        .background(MaterialTheme.colorScheme.secondary)
                ){
                    Text(
                        text = "This box is using secondary color and error text color",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Preview
@Composable
private fun ThemingPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = {
            ThemingScreen(
                viewModel = ThemingViewModel(null)
            )
        }
    )
}