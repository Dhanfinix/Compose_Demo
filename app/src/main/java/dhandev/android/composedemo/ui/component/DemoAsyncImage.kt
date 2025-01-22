package dhandev.android.composedemo.ui.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.ImageRequest
import dhandev.android.composedemo.R

@Composable
fun DemoAsyncImage(
    modifier: Modifier = Modifier,
    url: String
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(url)
            .crossfade(true)
            .build(),
        placeholder = painterResource(R.drawable.ic_launcher_background),
        contentDescription = null,
        contentScale = ContentScale.Crop
    )
}