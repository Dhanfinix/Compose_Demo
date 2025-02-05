package edts.android.composedemo.ui.modifier

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

val DefaultRoundedBgPadding = 16.dp
val DefaultRoundedBgCornerRadius = 16.dp

@Composable
fun Modifier.roundedBackground(
    color: Color = MaterialTheme.colorScheme.surfaceVariant,
    cornerRadius: Dp = DefaultRoundedBgCornerRadius,
    padding: Dp = DefaultRoundedBgPadding
): Modifier = this
    .clip(RoundedCornerShape(cornerRadius))
    .background(color)
    .padding(padding)

@Composable
fun RoundedColumn(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Center,
    content: @Composable() (ColumnScope.() -> Unit)
) {
    Column(
        modifier.roundedBackground(),
        verticalArrangement = verticalArrangement
    ) {
        content()
    }
}

@Composable
fun RoundedRow(
    modifier: Modifier = Modifier,
    vertcalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable() (RowScope.() -> Unit)
) {
    Row(
        modifier.roundedBackground(),
        verticalAlignment = vertcalAlignment
    ) {
        content()
    }
}