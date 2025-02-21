package edts.android.composedemo.ui.component

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import edts.android.composedemo.constants.ThemeMode
import edts.android.composedemo.domain.model.ArticleItemData
import edts.android.composedemo.ui.modifier.RoundedColumn
import edts.android.composedemo.ui.modifier.roundedBackground
import edts.android.composedemo.ui.modifier.shimmer
import edts.android.composedemo.ui.theme.InterFamily
import edts.android.composedemo.ui.theme.MontserratFamily
import edts.android.composedemo.ui.theme.body1
import edts.android.composedemo.ui.theme.headline3
import edts.android.composedemo.utils.DpUtils
import edts.android.composedemo.utils.preview.PreviewWrapperComp
import edts.android.composedemo.utils.preview.ThemePreviewProvider

@Composable
fun NewsItemComp(
    modifier: Modifier = Modifier,
    articleItemData: ArticleItemData
) {
    val context = LocalContext.current
    RoundedColumn(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                if (articleItemData.link.isNullOrBlank()) {
                    Toast.makeText(context, "Link Empty", Toast.LENGTH_SHORT).show()
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(articleItemData.link))
                    context.startActivity(intent)
                }
            }
    ) {
        Text(
            text = articleItemData.title ?: "No Title",
            style = MontserratFamily.headline3(),
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = articleItemData.content ?: "No Content",
            style = InterFamily.body1(),
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun ShimmerNewsItemComp(
    modifier: Modifier = Modifier
) {
    NewsItemComp(
        modifier = modifier
            .height(DpUtils.getRandomDp())
            .roundedBackground(padding = 0.dp)
            .shimmer(),
        articleItemData = ArticleItemData()
    )
}

@Preview
@Composable
private fun NewsItemPreview(
    @PreviewParameter(ThemePreviewProvider::class)
    themeMode: ThemeMode
) {
    PreviewWrapperComp(
        themeMode = themeMode
    ){
        NewsItemComp(
            articleItemData = ArticleItemData(
                title = "This is the title",
                content = "This is the content",
                link = "https://www.google.com"
            )
        )
    }
}

