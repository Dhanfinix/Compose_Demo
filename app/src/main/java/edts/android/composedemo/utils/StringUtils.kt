package edts.android.composedemo.utils

import android.content.Context
import android.net.Uri
import edts.android.composedemo.R

object StringUtils {
    fun isValidDeeplink(
        context: Context,
        deeplink: Uri?,
    ): Boolean {
        val schemes = context.getString(R.string.schemes)
        val host = context.getString(R.string.host)

        return deeplink?.scheme == schemes && deeplink.host == host
    }
}