package edts.android.composedemo.ui.component.segment_button

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MultiChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.util.fastForEachIndexed
import edts.android.composedemo.R
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.ui.theme.ComposeDemoTheme

@Composable
fun <T> SegmentedButtonComp(
    modifier: Modifier = Modifier,
    uiState: SegmentedState<T>,
    onChecked: (Int, T?) -> Unit,
) {
    if (uiState.isMulti) {
        MultiChoiceSegmentedButtonRow(modifier) {
            uiState.options.fastForEachIndexed { index, segmentedData ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, uiState.options.size),
                    icon = {
                        SegmentedButtonDefaults.Icon(segmentedData.checked) {
                            Icon(
                                painter = painterResource(segmentedData.icon),
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                            )
                        }
                    },
                    onCheckedChange = { onChecked(index, segmentedData.metadata) },
                    checked = segmentedData.checked,
                ) {
                    Text(segmentedData.title)
                }
            }
        }
    } else {
        SingleChoiceSegmentedButtonRow {
            uiState.options.fastForEachIndexed { index, segmentedData ->
                val selected = uiState.selectedIndex == index
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index, uiState.options.size),
                    icon = {
                        SegmentedButtonDefaults.Icon(selected) {
                            Icon(
                                painter = painterResource(segmentedData.icon),
                                contentDescription = null,
                                modifier = Modifier.size(SegmentedButtonDefaults.IconSize),
                            )
                        }
                    },
                    selected = selected,
                    onClick = { onChecked(index, segmentedData.metadata) },
                ) {
                    Text(segmentedData.title)
                }
            }
        }
    }
}

@Preview
@Composable
private fun SegmentedButtonPreview() {
    ComposeDemoTheme{
        SegmentedButtonComp(
            uiState =
                SegmentedState<ThemeMode>(
                    options =
                        listOf(
                            SegmentedData("Light", R.drawable.baseline_light_mode_24),
                            SegmentedData("Dark", R.drawable.baseline_dark_mode_24),
                            SegmentedData("Custom", R.drawable.baseline_person_4_24),
                            SegmentedData("System", R.drawable.baseline_auto_mode_24),
                        ),
                    selectedIndex = 2
                ),
        ) {_, _ -> }
    }
}
