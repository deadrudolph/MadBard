package com.deadrudolph.uicomponents.compose.theme

import androidx.compose.material.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.sp
import com.puls.uicomponents.R

val heeboFonts = FontFamily(
    Font(resId = R.font.heebo_thin, weight = FontWeight.W100),
    Font(resId = R.font.heebo_extra_light, weight = FontWeight.W200),
    Font(resId = R.font.heebo_light, weight = FontWeight.W300),
    Font(resId = R.font.heebo_regular, weight = FontWeight.W400),
    Font(resId = R.font.heebo_medium, weight = FontWeight.W500),
    Font(resId = R.font.heebo_semi_bold, weight = FontWeight.W600),
    Font(resId = R.font.heebo_bold, weight = FontWeight.W700),
    Font(resId = R.font.heebo_extra_bold, weight = FontWeight.W800),
    Font(resId = R.font.heebo_black, weight = FontWeight.W900)
)

@ExperimentalUnitApi
val Typography = Typography(
    h1 = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W500,
        fontSize = 22.sp,
        letterSpacing = TextUnit(0f, TextUnitType.Sp),
        lineHeight = 33.sp
    ),
    h2 = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W500,
        fontSize = 18.sp,
        letterSpacing = TextUnit(0f, TextUnitType.Sp),
        lineHeight = 24.sp
    ),

    body1 = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W500,
        fontSize = 16.sp,
        letterSpacing = TextUnit(0f, TextUnitType.Sp),
        lineHeight = 24.sp
    ),
    body2 = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W400,
        fontSize = 14.sp,
        letterSpacing = TextUnit(0f, TextUnitType.Sp),
        lineHeight = 21.sp
    ),

    button = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = TextUnit(1.4f, TextUnitType.Sp),
        lineHeight = 21.sp
    ),

    caption = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W700,
        fontSize = 10.sp,
        letterSpacing = TextUnit(1f, TextUnitType.Sp),
        lineHeight = 18.sp
    ),
)

@ExperimentalUnitApi
val LocalCustomTypography = staticCompositionLocalOf {
    customTypography
}

@ExperimentalUnitApi
val customTypography = CustomTypography(
    body3 = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W500,
        fontSize = 14.sp,
        letterSpacing = TextUnit(0f, TextUnitType.Sp),
        lineHeight = 21.sp
    ),
    caption2 = TextStyle(
        fontFamily = heeboFonts,
        fontWeight = FontWeight.W400,
        fontSize = 12.sp,
        letterSpacing = TextUnit(0f, TextUnitType.Sp),
        lineHeight = 18.sp
    )
)

@Immutable
data class CustomTypography(
    val body3: TextStyle,
    val caption2: TextStyle,
)