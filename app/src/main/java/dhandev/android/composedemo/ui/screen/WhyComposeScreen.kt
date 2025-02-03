package dhandev.android.composedemo.ui.screen

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
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui.component.NumberedValueItemComp

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
                Button(
                    onClick = { /*TODO*/ }
                ) {
                    Text("Go to XML version")
                }
            }
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

private fun getListData():List<String>{
    return listOf(
        "Mobile Engineer",
        "Frontend Engineer",
        "Backend Engineer",
        "Quality Assurance Engineer",
        "UX Engineer",
        "Product Designer",
        "Project Manager",
        "Others..."
    )
}