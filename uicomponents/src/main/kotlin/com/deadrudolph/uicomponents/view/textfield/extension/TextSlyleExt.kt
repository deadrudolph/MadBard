package com.deadrudolph.uicomponents.view.textfield.extension

import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.isUnspecified
import com.deadrudolph.uicomponents.view.textfield.core.constants.DefaultBackgroundColor
import com.deadrudolph.uicomponents.view.textfield.core.constants.DefaultColor
import com.deadrudolph.uicomponents.view.textfield.core.constants.DefaultFontSize
import com.deadrudolph.uicomponents.view.textfield.core.constants.DefaultLetterSpacing
import com.deadrudolph.uicomponents.view.textfield.core.constants.emptyTextTransform
import com.deadrudolph.uicomponents.view.textfield.core.locale.LocaleList
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.span.TextForegroundStyle
import com.deadrudolph.uicomponents.view.textfield.core.style.TextDecoration

@OptIn(ExperimentalTextApi::class)
internal fun resolveSpanStyleDefaults(style: SpanStyle) = SpanStyle(
    textForegroundStyle = style.textForegroundStyle.takeOrElse {
        TextForegroundStyle.from(DefaultColor)
    },
    fontSize = if (style.fontSize.isUnspecified) DefaultFontSize else style.fontSize,
    fontWeight = style.fontWeight ?: FontWeight.Normal,
    fontStyle = style.fontStyle ?: FontStyle.Normal,
    fontSynthesis = style.fontSynthesis ?: FontSynthesis.All,
    fontFamily = style.fontFamily ?: FontFamily.Default,
    fontFeatureSettings = style.fontFeatureSettings ?: "",
    letterSpacing = if (style.letterSpacing.isUnspecified) {
        DefaultLetterSpacing
    } else {
        style.letterSpacing
    },
    baselineShift = style.baselineShift ?: BaselineShift.None,
    textGeometricTransform = style.textGeometricTransform ?: emptyTextTransform,
    localeList = style.localeList ?: LocaleList.current,
    background = style.background.takeOrElse { DefaultBackgroundColor },
    textDecoration = style.textDecoration ?: TextDecoration.None,
    shadow = style.shadow ?: Shadow.None,
    platformStyle = style.platformStyle,
    drawStyle = style.drawStyle ?: Fill
)

inline fun unpackInt1(value: Long): Int {
    return value.shr(32).toInt()
}

/**
 * Unpacks the second Int value in [packInts] from its returned ULong.
 */
inline fun unpackInt2(value: Long): Int {
    return value.and(0xFFFFFFFF).toInt()
}

inline fun packInts(val1: Int, val2: Int): Long {
    return val1.toLong().shl(32) or (val2.toLong() and 0xFFFFFFFF)
}