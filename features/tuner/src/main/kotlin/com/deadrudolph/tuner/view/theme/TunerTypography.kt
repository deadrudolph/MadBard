package com.deadrudolph.tuner.view.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import com.deadrudolph.tuner.R

object TunerTypography {

    private val notoSansFontFamily = FontFamily(Font(R.font.noto_sans))

    operator fun invoke() =
        Typography(
            defaultFontFamily = notoSansFontFamily
        )
}
