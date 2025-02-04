package edts.android.composedemo.ui.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import edts.android.composedemo.R

val MontserratFamily =
    FontFamily(
        Font(R.font.montserrat_regular, FontWeight.Normal),
        Font(R.font.montserrat_medium, FontWeight.Medium),
        Font(R.font.montserrat_bold, FontWeight.Bold),
        Font(R.font.montserrat_black, FontWeight.Black),
    )

val InterFamily =
    FontFamily(
        Font(R.font.inter_regular, FontWeight.Normal),
        Font(R.font.inter_medium, FontWeight.Medium),
        Font(R.font.inter_bold, FontWeight.Bold),
        Font(R.font.inter_black, FontWeight.Black),
    )

fun FontFamily.headline1() =
    TextStyle(
        fontFamily = this,
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 38.sp,
    )

fun FontFamily.headline2() =
    TextStyle(
        fontFamily = this,
        fontSize = 24.sp,
        fontWeight = FontWeight.SemiBold,
        lineHeight = 32.sp,
    )

fun FontFamily.headline3() =
    TextStyle(
        fontFamily = this,
        fontSize = 22.sp,
        fontWeight = FontWeight.SemiBold,
        fontSynthesis = FontSynthesis.Weight,
        lineHeight = 30.sp,
    )

fun FontFamily.body1() =
    TextStyle(
        fontFamily = this,
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 24.sp,
    )

fun FontFamily.body2() =
    TextStyle(
        fontFamily = this,
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 20.sp,
    )

fun FontFamily.subtitle1() =
    TextStyle(
        fontFamily = this,
        fontSize = 16.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 24.sp,
    )

fun FontFamily.subtitle2() =
    TextStyle(
        fontFamily = this,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        lineHeight = 20.sp,
    )

fun FontFamily.caption() =
    TextStyle(
        fontFamily = this,
        fontSize = 12.sp,
        fontWeight = FontWeight.Normal,
        lineHeight = 16.sp,
    )

fun FontFamily.button() =
    TextStyle(
        fontFamily = this,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        lineHeight = 16.sp,
    )
