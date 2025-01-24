package dhandev.android.composedemo.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import dhandev.android.composedemo.R
import dhandev.android.composedemo.ui.component.DemoAsyncImageComp
import dhandev.android.composedemo.ui.component.DemoScaffoldComp

/**
 * Demonstrates some basic components and layout in Jetpack Compose.
 *
 * @param modifier Modifier to be applied to the Scaffold.
 */
@Composable
fun SimpleComponentScreen(modifier: Modifier = Modifier) {
    val imageUrls = listOf(
        "https://images.unsplash.com/photo-1735818978089-85970221e678?q=80&h=400&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1736931544273-ebe70ecc8829?q=80&h=400&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        "https://images.unsplash.com/photo-1736849489023-19b4751b47fc?q=80&h=400&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
    )
    var showLoading by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    DemoScaffoldComp(
        modifier = modifier,
        title = "Simple Component"
    ) { innerPadding ->
        Box(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("This text created with only one line")

                // Loading image from drawable resource
                Text("These image loaded from drawable")
                Image(
                    painter = painterResource(R.drawable.tugu_jogja),
                    contentDescription = null,
                    modifier = Modifier.height(100.dp),
                    contentScale = ContentScale.Crop
                )

                // Loading images from URLs using Coil
                Text("These image loaded from a url with coil")
                Row {
                    imageUrls.forEach {
                        DemoAsyncImageComp(url = it)
                    }
                }

                // Using LazyRow for scrolling
                Text("Above image can't be scrolled, change it to LazyRow like below")
                LazyRow {
                    items(imageUrls) {
                        DemoAsyncImageComp(url = it)
                    }
                }

                // Demonstrating Box layout with a button and loading indicator
                Text("To demonstrate box layout, click this button below")
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        showLoading = !showLoading
                    }
                ) {
                    Text(when (showLoading) {
                        true -> "Hide Loading"
                        false -> "Show Loading"
                    })
                }
            }

            // Display CircularProgressIndicator when showLoading is true
            if (showLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
