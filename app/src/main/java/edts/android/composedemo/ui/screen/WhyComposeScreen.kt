package edts.android.composedemo.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.LocalActivity
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.constants.getListData
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.ui.component.NumberedValueItemComp
import edts.android.composedemo.ui_view.list_item.ListItemActivity
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

@Composable
fun WhyComposeScreen(
    modifier: Modifier = Modifier
) {
    val data = getListData()
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.WhyCompose().title
    ) {innerPadding->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Text("Here we want to demonstrate how compose can simplify " +
                        "the UI development, especially for list items. " +
                        "To fully see the difference, please see the source code")
            }
            item {
                val activity = LocalActivity.current
                Button(
                    onClick = {
                        activity?.let { ListItemActivity.open(it) }
                    }
                ) {
                    Text("Go to XML version")
                }
            }
            //Char and line count is only on related part, excluding import and data
            item {
                HorizontalDivider()
                Text("Below is the compose version, total 947 Char (Include space) and 31 line breaks")
            }
            itemsIndexed(data){index, data->
                val context = LocalContext.current
                NumberedValueItemComp(
                    number = index+1,
                    value = data
                ){
                    Toast.makeText(context, data, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

@Preview
@Composable
private fun WhyComposePreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = { WhyComposeScreen() }
    )
}