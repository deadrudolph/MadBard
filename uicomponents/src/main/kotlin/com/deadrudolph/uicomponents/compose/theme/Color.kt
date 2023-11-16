package com.deadrudolph.uicomponents.compose.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val LocalCustomLightColor = staticCompositionLocalOf { CustomColor.Light() }
val LocalCustomDarkColor = staticCompositionLocalOf { CustomColor.Dark() }

sealed class DefaultColor {

    abstract val primary: Color
    abstract val primaryVariant: Color
    abstract val secondary: Color
    abstract val secondaryVariant: Color
    abstract val background: Color
    abstract val surface: Color
    abstract val error: Color
    abstract val onPrimary: Color
    abstract val onSecondary: Color
    abstract val onBackground: Color
    abstract val onSurface: Color
    abstract val onError: Color

    class Light : DefaultColor() {
        override val primary: Color = Color(0xFF000000)
        override val primaryVariant: Color = Color(0x4372F566)
        override val secondary: Color = Color(0xFF939393)
        override val secondaryVariant: Color = Color(0xFF555555)
        override val background: Color = Color(0xFF0C0C0C)
        override val surface: Color = Color(0xFF1A1A1A)
        override val error: Color = Color(0xFFF95733)
        override val onPrimary: Color = Color(0xFF3C3B3B)
        override val onSecondary: Color = Color(0xFFFFFF66)
        override val onBackground: Color = Color(0xFFFFFFFF)
        override val onSurface: Color = Color(0xFFECE5E5)
        override val onError: Color = Color(0xFFF95733)
    }

    open class Dark : DefaultColor() {
        override val primary: Color = Color(0xFF000000)
        override val primaryVariant: Color = Color(0x4372F566)
        override val secondary: Color = Color(0xFF939393)
        override val secondaryVariant: Color = Color(0xFF555555)
        override val background: Color = Color(0xFF151515)
        override val surface: Color = Color(0xFF939393)
        override val error: Color = Color(0xFFF95733)
        override val onPrimary: Color = Color(0xFF3C3B3B)
        override val onSecondary: Color = Color(0xFFFFFF66)
        override val onBackground: Color = Color(0xFFECE5E5)
        override val onSurface: Color = Color(0x292929A6)
        override val onError: Color = Color(0xFFF95733)
    }
}

/**
 * Colors from design - https://www.figma.com/file/yGLnNbEuvvJo7miDZFIaxR/MistPlay-UI?node-id=213%3A1280
 * */

sealed class CustomColor {
    abstract val backgroundColor: Color

    //Primary
    abstract val primary_inactive_45: Color
    abstract val primary_contrast_dark_blue: Color
    abstract val primary_contrast_red: Color
    abstract val primary_700: Color

    //White
    abstract val white_100: Color
    abstract val white_100_33: Color
    abstract val white_100_40: Color
    abstract val white_300: Color
    abstract val white_400: Color
    abstract val white_500_32: Color

    //Dark
    abstract val dark_600: Color
    abstract val dark_700_65: Color
    abstract val dark_800: Color
    abstract val dark_850_80: Color
    abstract val dark_900_linear: Pair<Color, Color>
    abstract val dark_900_bg: Color

    //Other
    abstract val yellow: Color
    abstract val yellow_50: Color
    abstract val red: Color
    abstract val red_50: Color
    abstract val violet: Color
    abstract val violet_50: Color
    abstract val warning_red: Color

    open class Light : CustomColor() {
        //TODO example
        //Primary
        override val primary_700 = Color(0xFF203775)
        override val primary_inactive_45 = Color(0x4372F566)
        override val primary_contrast_dark_blue = Color(0xFF4372F5)
        override val primary_contrast_red = Color(0xFFF62C3E)

        //White
        override val white_100 = Color(0xFFFFFFFF)
        override val white_100_33 = Color(0x33FFFFFF)
        override val white_100_40 = Color(0xFFFFFF66)
        override val white_300 = Color(0xFFF5F5F5)
        override val white_400 = Color(0xFFDADADA)
        override val white_500_32 = Color(0x4F4F4F52)

        //Dark
        override val dark_600 = Color(0xFF939393)
        override val dark_700_65 = Color(0xE9060631)
        override val dark_800 = Color(0xFF212121)
        override val dark_850_80 = Color(0x040E29CC)
        override val dark_900_linear = Pair(
            first = Color(0xFF000000),
            second = Color(0x00000000),
        )
        final override val dark_900_bg = Color(0xFF151515)

        //Other
        override val yellow = Color(0xFFF9B233)
        override val yellow_50 = Color(0x80F9B233)
        override val red = Color(0xFFF95733)
        override val red_50 = Color(0x80F95733)
        override val violet = Color(0xFF8A33F9)
        override val violet_50 = Color(0x808A33F9)
        override val backgroundColor: Color = dark_900_bg
        override val warning_red: Color = Color(0x48FF3A3A)
    }

    //TODO if you necessary darkThem, you have to inherit CustomColor
    open class Dark : Light()
}
