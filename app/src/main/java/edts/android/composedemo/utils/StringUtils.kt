package edts.android.composedemo.utils

import android.content.Context
import android.net.Uri
import edts.android.composedemo.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object StringUtils {
    fun isValidDeeplink(
        context: Context,
        deeplink: Uri?,
    ): Boolean {
        val schemes = context.getString(R.string.schemes)
        val host = context.getString(R.string.host)

        return deeplink?.scheme == schemes && deeplink.host == host
    }

    fun getCurrentDate(): String{
        val currentDate = Calendar.getInstance()
        val formatter = SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault())
        val formattedDate = formatter.format(currentDate.time)
        return formattedDate
    }
}