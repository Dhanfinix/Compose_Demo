package edts.android.composedemo.ui_view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import edts.android.composedemo.databinding.ComposeInViewBinding
import edts.android.composedemo.ui.component.CounterComp
import edts.android.composedemo.utils.preview.PreviewWrapperComp

/**
 * This activity is extending AppCompatActivity, compare to MainActivity
 * which extending ComponentActivity.
 * To Put it simply, AppCompatActivity is a subclass of ComponentActivity
 * and it's has better support for older Android versions.
 * While ComponentActivity is a more general class that can be used
 * for compose-based activity.
 * @see<a href=https://stackoverflow.com/questions/67891362/componentactivity-vs-appcompatactivity-in-android-jetpack-compose>Stack Overflow</a>
 */
class ComposeInViewActivity : AppCompatActivity() {
    private lateinit var binding: ComposeInViewBinding

    companion object{
        fun open(activity: Activity){
            val intent = Intent(activity, ComposeInViewActivity::class.java)
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ComposeInViewBinding.inflate(layoutInflater)

        // Set the content of the activity
        setContentView(binding.root)
        // or you can just use setContent{} if it's fully in compose

        // Set up the ComposeView
        binding.composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ComposeGreeting()
                // You can add more Compose composables here
            }
        }
    }
}
@Composable
private fun ComposeGreeting() {
    Column {
        Text("Hello from Compose!")
        Spacer(Modifier.height(8.dp))
        CounterComp()
    }
}

@Preview
@Composable
fun ComposeGreetingPreview() {
    PreviewWrapperComp(
        content = {
            ComposeGreeting()
        }
    )
}
