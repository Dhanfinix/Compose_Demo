package dhandev.android.composedemo.data

import androidx.compose.runtime.saveable.Saver
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class LoginDataKotlin(
    val username: String = "",
    val password: String = ""
)

val loginDataSaver = Saver<LoginDataKotlin, String>(
    save = { Json.encodeToString(it) },
    restore = { Json.decodeFromString(it) }
)
