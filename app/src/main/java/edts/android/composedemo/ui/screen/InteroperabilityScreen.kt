package edts.android.composedemo.ui.screen

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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.widget.addTextChangedListener
import edts.android.composedemo.constants.Destinations
import edts.android.composedemo.constants.LocalActivity
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.databinding.ViewInComposeBinding
import edts.android.composedemo.ui.component.DemoScaffoldComp
import edts.android.composedemo.ui_view.ComposeInViewActivity
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider
import id.co.edtslib.edtsds.ButtonView
import id.co.edtslib.edtsds.R
import id.co.edtslib.edtsds.StrikeTextView
import edts.android.composedemo.R as AppResource
import id.co.edtslib.edtsds.ratingview.RatingDelegate
import id.co.edtslib.edtsds.ratingview.RatingView

@Composable
fun InteroperabilityScreen(
    modifier: Modifier = Modifier
) {
    val activity = if (LocalInspectionMode.current) null else LocalActivity.current
    val context = LocalContext.current
    var selectedRating by rememberSaveable { mutableStateOf<Int?>(null) }
    var enableButton by rememberSaveable { mutableStateOf(false) }
    var username by rememberSaveable { mutableStateOf("") }
    DemoScaffoldComp(
        modifier = modifier,
        title = Destinations.Interoperability().title,
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
                            text = it.getString(edts.android.composedemo.R.string.strik_text)
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
                        activity?.let { ComposeInViewActivity.open(it) }
                    }
                ) {
                    Text("Go to View XML Layout")
                }
            }
        }
    }
}

@Preview
@Composable
private fun InteroperabilityPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode,
        content = { InteroperabilityScreen() }
    )
}