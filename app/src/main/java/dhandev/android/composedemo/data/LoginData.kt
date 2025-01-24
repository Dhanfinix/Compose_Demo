package dhandev.android.composedemo.data

import java.io.Serializable

data class LoginData(
    val username: String = "",
    val password: String = ""
): Serializable
