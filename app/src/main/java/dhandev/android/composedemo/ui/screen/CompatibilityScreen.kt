package dhandev.android.composedemo.ui.screen

import android.widget.LinearLayout
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.widget.addTextChangedListener
import dhandev.android.composedemo.constants.Destinations
import dhandev.android.composedemo.constants.LocalActivity
import dhandev.android.composedemo.databinding.ViewInComposeBinding
import dhandev.android.composedemo.ui.component.DemoScaffoldComp
import dhandev.android.composedemo.ui_view.ComposeInViewActivity
import id.co.edtslib.edtsds.ButtonView
import id.co.edtslib.edtsds.R
import id.co.edtslib.edtsds.StrikeTextView
import dhandev.android.composedemo.R as AppResource
import id.co.edtslib.edtsds.ratingview.RatingDelegate
import id.co.edtslib.edtsds.ratingview.RatingView

@Composable
fun CompatibilityScreen(
    modifier: Modifier = Modifier
) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    var selectedRating by rememberSaveable { mutableStateOf<Int?>(null) }
    var enableButton by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.Compatibility().title,
    ) {innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ){
            item {
                Text("These component below is composable combination with View-based component!")
            }
            item {
                Row {
                    AndroidView(
                        modifier = Modifier.weight(1f),
                        factory = {
                            ButtonView(it).apply {
                                id = AppResource.id.btn_view
                                text = "EDTS DS Button View"
                                setOnClickListener {
                                    Toast.makeText(
                                        context,
                                        "This button isn't composable",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                variant = ButtonView.ButtonVariant.Secondary

                                layoutParams = LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                ).apply {
                                    bottomMargin = resources.getDimensionPixelSize(R.dimen.dimen_8dp)
                                }
                            }
                        },
                        update = {
                            val btn = it.findViewById<ButtonView>(AppResource.id.btn_view)
                            btn.isEnabled = enableButton
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    Switch(
                        checked = enableButton,
                        onCheckedChange = {enableButton = it}
                    )
                }
            }
            item {
                AndroidView(
                    modifier = Modifier.padding(vertical = 8.dp),
                    factory = {
                        RatingView(it).apply {
                            id = AppResource.id.rating_view
                            count = 5
                            delegate = object : RatingDelegate {
                                override fun onChanged(value: Int) {
                                    selectedRating = value
                                }
                            }
                        }
                    },
                    update = {
                        val ratingView = it.findViewById<RatingView>(AppResource.id.rating_view)
                        selectedRating?.let {value->
                            ratingView.value = value
                        }
                    }
                )
            }
            item {
                Text("Selected Rating : $selectedRating")
            }
            item {
                AndroidView(
                    factory = {
                        StrikeTextView(it).apply {
                            text = it.getString(dhandev.android.composedemo.R.string.strik_text)
                        }
                    }
                )
            }
            item {
                HorizontalDivider()
                Text("Maybe you want to add full XML layout in composable? You can do it too")
            }
            item {
                AndroidViewBinding(ViewInComposeBinding::inflate){
                    etName.addTextChangedListener {
                        username = it.toString()
                    }

                    tvName.text = username.ifEmpty { "Username" }
                }
            }
            item {
                HorizontalDivider()
                Text("Btw we also can use composable inside a View XML Layout")
                Button(
                    onClick = {
                        ComposeInViewActivity.open(activity)
                    }
                ) {
                    Text("Go to View XML Layout")
                }
            }
        }
    }
}