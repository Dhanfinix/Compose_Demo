package edts.android.composedemo.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edts.android.composedemo.R
import edts.android.composedemo.ui.theme.ColorNeutral30
import edts.android.composedemo.utils.preview.PreviewWrapperComp

@Composable
fun NumberedValueItemComp(
    modifier: Modifier = Modifier,
    number: Int,
    value: String,
    onClick: ()->Unit
) {
    Row(
        modifier = modifier
            .background(ColorNeutral30, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 12.dp)
    ) {
        Text(
            text ="$number. $value",
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(R.drawable.arrow_right),
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun NumberedValueItemPreview() {
    PreviewWrapperComp(
        content = {
            NumberedValueItemComp(
                number = 1,
                value = "Test"
            ){}
        }
    )
}