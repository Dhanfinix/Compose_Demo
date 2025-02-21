package edts.android.composedemo.domain.model

import java.io.Serializable

data class LoginData(
    val username: String = "",
    val password: String = ""
): Serializable
