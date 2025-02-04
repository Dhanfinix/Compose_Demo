package edts.android.composedemo.ui.component.side_effect

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun NetworkStatusObserver(
    onNetworkAvailable: (Boolean) -> Unit
) {
    val context = LocalContext.current

    DisposableEffect(Unit) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                onNetworkAvailable(true)
            }

            override fun onLost(network: Network) {
                onNetworkAvailable(false)
            }
        }

        val request =  NetworkRequest.Builder().build()
        connectivityManager.registerNetworkCallback(request, networkCallback)

        onDispose {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            Toast.makeText(context, "Network Observer Removed", Toast.LENGTH_SHORT).show()
        }
    }
}