package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.style.CharacterStyle
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.Placeholder
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle
import androidx.compose.ui.text.android.InternalPlatformTextApi
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.isUnspecified
import androidx.emoji2.text.EmojiCompat
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.style.LineHeightStyle
import com.deadrudolph.uicomponents.view.textfield.core.style.TextDecoration
import com.deadrudolph.uicomponents.view.textfield.extension.isIncludeFontPaddingEnabled
import com.deadrudolph.uicomponents.view.textfield.extension.setLineHeight
import com.deadrudolph.uicomponents.view.textfield.extension.setPlaceholders
import com.deadrudolph.uicomponents.view.textfield.extension.setSpan
import com.deadrudolph.uicomponents.view.textfield.extension.setSpanStyles
import com.deadrudolph.uicomponents.view.textfield.extension.setTextIndent

private val NoopSpan = object : CharacterStyle() {
    override fun updateDrawState(p0: TextPaint?) {}
}

@OptIn(InternalPlatformTextApi::class, ExperimentalTextApi::class)
internal fun createCharSequence(
    text: String,
    contextFontSize: Float,
    contextTextStyle: TextStyle,
    spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    placeholders: List<AnnotatedString.Range<Placeholder>>,
    density: Density,
    resolveTypeface: (FontFamily?, FontWeight, FontStyle, FontSynthesis) -> Typeface,
    useEmojiCompat: Boolean,
): CharSequence {

    val currentText = if (useEmojiCompat && EmojiCompat.isConfigured()) {
        EmojiCompat.get().process(text)!!
    } else {
        text
    }

    if (spanStyles.isEmpty() &&
        placeholders.isEmpty() &&
        contextTextStyle.textIndent == TextIndent.None &&
        contextTextStyle.lineHeight.isUnspecified
    ) {
        return currentText
    }

    val spannableString = if (currentText is Spannable) {
        currentText
    } else {
        SpannableString(currentText)
    }

    // b/199939617
    // Due to a bug in the platform's native drawText stack, some CJK characters cause a bolder
    // than intended underline to be painted when TextDecoration is set to Underline.
    // If there's a CharacterStyle span that takes the entire length of the text, even if
    // it's no-op, it causes a different native call to render the text that prevents the bug.
    if (contextTextStyle.textDecoration == TextDecoration.Underline) {
        spannableString.setSpan(NoopSpan, 0, text.length)
    }

    if (contextTextStyle.isIncludeFontPaddingEnabled() &&
        contextTextStyle.lineHeightStyle == null
    ) {
        // keep the existing line height behavior for includeFontPadding=true
        spannableString.setLineHeight(
            lineHeight = contextTextStyle.lineHeight,
            contextFontSize = contextFontSize,
            density = density
        )
    } else {
        val lineHeightStyle = contextTextStyle.lineHeightStyle ?: LineHeightStyle.Default
        spannableString.setLineHeight(
            lineHeight = contextTextStyle.lineHeight,
            lineHeightStyle = lineHeightStyle,
            contextFontSize = contextFontSize,
            density = density,
        )
    }

    spannableString.setTextIndent(contextTextStyle.textIndent, contextFontSize, density)

    spannableString.setSpanStyles(
        contextTextStyle,
        spanStyles,
        density,
        resolveTypeface
    )

    spannableString.setPlaceholders(placeholders, density)

    return spannableString
}
