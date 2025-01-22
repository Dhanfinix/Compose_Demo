package dhandev.android.composedemo.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.ui.component.DemoAsyncImage
import dhandev.android.composedemo.ui.component.DemoScaffoldComp

@Composable
fun SimpleComponentScreen(modifier: Modifier = Modifier) {
    val imageUrls = listOf(
        "https://images.unsplash.com/photo-1735818978089-85970221e678?q=80&h=400&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1736931544273-ebe70ecc8829?q=80&h=400&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1736849489023-19b4751b47fc?q=80&h=400&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    )
    DemoScaffoldComp(
        modifier = modifier,
        title = "Simple Component",
        isLargeTopAppBar = false
    ) {innerPadding->
        Box(Modifier.padding(innerPadding).fillMaxSize()) {
            Column(
                Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("This text created with only one line")
                Text("These image loaded from a url with coil")
                Row {
                    imageUrls.forEach {
                        DemoAsyncImage(url = it)
                    }
                }
                Text("Above image can't be scrolled, change it to LazyRow like below")
                LazyRow {
                    items(imageUrls){
                        DemoAsyncImage(url = it)
                    }
                }
            }
        }
    }
}