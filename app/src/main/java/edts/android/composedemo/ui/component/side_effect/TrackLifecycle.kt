package edts.android.composedemo.ui.component.side_effect

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import edts.android.composedemo.constants.LocalScreenName

/**
 * This lifecycle track composable is based on lifecycle owner.
 *
 * In single activity approach in which we use Navigation lib, the
 * lifecycle owner is NavBackStackEntry, it never reach on-destroy,
 * just until on-stop. Some notes:
 * - When back to prev screen, it will on-create again, not on-resume
 * - So, the lifecycle is one way only on-create until on-stop
 *
 * In multi-activity approach, the lifecycle owner is Activity,
 *
 * In fragment approach, the lifecycle owner is Fragment.
 */
@Composable
fun TrackLifecycle() {
    val lifecycleOwner = LocalLifecycleOwner.current
    val screenName = LocalScreenName.current
    Log.d(TAG,"Who's the owner? its = $lifecycleOwner")

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when(event){
                Lifecycle.Event.ON_CREATE -> {
                    Log.d(TAG, "On Create Screen <$screenName>")
                }
                Lifecycle.Event.ON_START -> {
                    Log.d(TAG,"On Start Screen <$screenName>")
                }
                Lifecycle.Event.ON_RESUME -> {
                    Log.d(TAG,"On Resume Screen <$screenName>")
                }
                Lifecycle.Event.ON_PAUSE -> {
                    Log.d(TAG,"On Pause Screen <$screenName>")
                }
                Lifecycle.Event.ON_STOP -> {
                    Log.d(TAG,"On Stop Screen <$screenName>")
                }
                Lifecycle.Event.ON_DESTROY -> {
                    Log.d(TAG,"On Destroy Screen <$screenName>")
                }
                else -> {
                    Log.d(TAG,"Unhandled lifecycle event $event for screen <$screenName>")
                }
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}
private const val TAG = "LIFECYCLE"