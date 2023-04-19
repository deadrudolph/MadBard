package com.deadrudolph.uicomponents.view.textfield.extension

import android.graphics.Typeface
import android.os.Build
import android.text.Spannable
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.text.style.LeadingMarginSpan
import android.text.style.LocaleSpan
import android.text.style.MetricAffectingSpan
import android.text.style.RelativeSizeSpan
import android.text.style.ScaleXSpan
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.isSpecified
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.android.InternalPlatformTextApi
import androidx.compose.ui.text.android.style.BaselineShiftSpan
import androidx.compose.ui.text.android.style.FontFeatureSpan
import androidx.compose.ui.text.android.style.LetterSpacingSpanEm
import androidx.compose.ui.text.android.style.LetterSpacingSpanPx
import androidx.compose.ui.text.android.style.LineHeightSpan
import androidx.compose.ui.text.android.style.LineHeightStyleSpan
import androidx.compose.ui.text.android.style.PlaceholderSpan
import androidx.compose.ui.text.android.style.ShadowSpan
import androidx.compose.ui.text.android.style.SkewXSpan
import androidx.compose.ui.text.android.style.TextDecorationSpan
import androidx.compose.ui.text.android.style.TypefaceSpan
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.unit.sp
import androidx.emoji2.text.EmojiSpan
import com.deadrudolph.uicomponents.view.textfield.core.DrawStyleSpan
import com.deadrudolph.uicomponents.view.textfield.core.locale.Locale
import com.deadrudolph.uicomponents.view.textfield.core.locale.LocaleList
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.style.LineHeightStyle
import com.deadrudolph.uicomponents.view.textfield.core.style.TextDecoration
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle
import kotlin.math.ceil
import kotlin.math.roundToInt

internal fun Spannable.setSpan(span: Any, start: Int, end: Int) {
    setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
}

@OptIn(InternalPlatformTextApi::class)
internal fun Spannable.setLineHeight(
    lineHeight: TextUnit,
    contextFontSize: Float,
    density: Density
) {
    val resolvedLineHeight = resolveLineHeightInPx(lineHeight, contextFontSize, density)
    if (!resolvedLineHeight.isNaN()) {
        setSpan(
            span = LineHeightSpan(lineHeight = resolvedLineHeight),
            start = 0,
            end = length
        )
    }
}

private fun resolveLineHeightInPx(
    lineHeight: TextUnit,
    contextFontSize: Float,
    density: Density
): Float {
    return when (lineHeight.type) {
        TextUnitType.Sp -> with(density) { lineHeight.toPx() }
        TextUnitType.Em -> lineHeight.value * contextFontSize
        else -> Float.NaN
    }
}

@OptIn(InternalPlatformTextApi::class, ExperimentalTextApi::class)
internal fun Spannable.setLineHeight(
    lineHeight: TextUnit,
    contextFontSize: Float,
    density: Density,
    lineHeightStyle: LineHeightStyle
) {
    val resolvedLineHeight = resolveLineHeightInPx(
        lineHeight,
        contextFontSize,
        density
    )
    if (!resolvedLineHeight.isNaN()) {
        // in order to handle empty lines (including empty text) better, change endIndex so that
        // it won't apply trimLastLineBottom rule
        val endIndex = if (isEmpty() || last() == '\n') length + 1 else length
        setSpan(
            span = LineHeightStyleSpan(
                lineHeight = resolvedLineHeight,
                startIndex = 0,
                endIndex = endIndex,
                trimFirstLineTop = lineHeightStyle.trim.isTrimFirstLineTop(),
                trimLastLineBottom = lineHeightStyle.trim.isTrimLastLineBottom(),
                topRatio = lineHeightStyle.alignment.topRatio
            ),
            start = 0,
            end = length
        )
    }
}

@Suppress("DEPRECATION")
internal fun Spannable.setTextIndent(
    textIndent: TextIndent?,
    contextFontSize: Float,
    density: Density
) {
    textIndent?.let { indent ->
        if (indent.firstLine == 0.sp && indent.restLine == 0.sp) return@let
        if (indent.firstLine.isUnspecified || indent.restLine.isUnspecified) return@let
        with(density) {
            val firstLine = when (indent.firstLine.type) {
                TextUnitType.Sp -> indent.firstLine.toPx()
                TextUnitType.Em -> indent.firstLine.value * contextFontSize
                else -> 0f
            }
            val restLine = when (indent.restLine.type) {
                TextUnitType.Sp -> indent.restLine.toPx()
                TextUnitType.Em -> indent.restLine.value * contextFontSize
                else -> 0f
            }
            setSpan(
                LeadingMarginSpan.Standard(
                    ceil(firstLine).toInt(),
                    ceil(restLine).toInt()
                ),
                0,
                length
            )
        }
    }
}

internal fun Spannable.setSpanStyles(
    contextTextStyle: TextStyle,
    spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    density: Density,
    resolveTypeface: (FontFamily?, FontWeight, FontStyle, FontSynthesis) -> Typeface,
) {

    setFontAttributes(contextTextStyle, spanStyles, resolveTypeface)
    var hasLetterSpacing = false
    for (i in spanStyles.indices) {
        val spanStyleRange = spanStyles[i]
        val start = spanStyleRange.start
        val end = spanStyleRange.end

        if (start < 0 || start >= length || end <= start || end > length) continue

        setSpanStyle(
            spanStyleRange,
            density
        )

        if (spanStyleRange.item.needsLetterSpacingSpan) {
            hasLetterSpacing = true
        }
    }

    if (hasLetterSpacing) {

        // LetterSpacingSpanPx/LetterSpacingSpanSP has lower priority than normal spans. Because
        // letterSpacing relies on the fontSize on [Paint] to compute Px/Sp from Em. So it must be
        // applied after all spans that changes the fontSize.

        for (i in spanStyles.indices) {
            val spanStyleRange = spanStyles[i]
            val start = spanStyleRange.start
            val end = spanStyleRange.end
            val style = spanStyleRange.item

            if (start < 0 || start >= length || end <= start || end > length) continue

            createLetterSpacingSpan(style.letterSpacing, density)?.let {
                setSpan(it, start, end)
            }
        }
    }
}

@OptIn(InternalPlatformTextApi::class)
internal fun Spannable.setFontAttributes(
    contextTextStyle: TextStyle,
    spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    resolveTypeface: (FontFamily?, FontWeight, FontStyle, FontSynthesis) -> Typeface,
) {
    val fontRelatedSpanStyles = spanStyles.fastFilter {
        it.item.hasFontAttributes() || it.item.fontSynthesis != null
    }

    // Create a SpanStyle if contextTextStyle has font related attributes, otherwise use
    // null to avoid unnecessary object creation.
    val contextFontSpanStyle = if (contextTextStyle.hasFontAttributes()) {
        SpanStyle(
            fontFamily = contextTextStyle.fontFamily,
            fontWeight = contextTextStyle.fontWeight,
            fontStyle = contextTextStyle.fontStyle,
            fontSynthesis = contextTextStyle.fontSynthesis
        )
    } else {
        null
    }

    flattenFontStylesAndApply(
        contextFontSpanStyle,
        fontRelatedSpanStyles
    ) { spanStyle, start, end ->
        setSpan(
            TypefaceSpan(
                resolveTypeface(
                    spanStyle?.fontFamily,
                    spanStyle?.fontWeight ?: FontWeight.Normal,
                    spanStyle?.fontStyle ?: FontStyle.Normal,
                    spanStyle?.fontSynthesis ?: FontSynthesis.All
                )
            ),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }
}

@OptIn(ExperimentalTextApi::class)
private fun Spannable.setSpanStyle(
    spanStyleRange: AnnotatedString.Range<SpanStyle>,
    density: Density
) {
    val start = spanStyleRange.start
    val end = spanStyleRange.end
    val style = spanStyleRange.item

    // Be aware that SuperscriptSpan needs to be applied before all other spans which
    // affect FontMetrics
    setBaselineShift(style.baselineShift, start, end)

    setColor(style.color, start, end)

    setBrush(style.brush, style.alpha, start, end)

    setTextDecoration(style.textDecoration, start, end)

    setFontSize(style.fontSize, density, start, end)

    setFontFeatureSettings(style.fontFeatureSettings, start, end)

    setGeometricTransform(style.textGeometricTransform, start, end)

    setLocaleList(style.localeList, start, end)

    setBackground(style.background, start, end)

    setShadow(style.shadow, start, end)

    setDrawStyle(style.drawStyle, start, end)
}

@OptIn(InternalPlatformTextApi::class)
private fun Spannable.setBaselineShift(baselineShift: BaselineShift?, start: Int, end: Int) {
    baselineShift?.let {
        setSpan(BaselineShiftSpan(it.multiplier), start, end)
    }
}

internal fun Spannable.setColor(color: Color, start: Int, end: Int) {
    if (color.isSpecified) {
        setSpan(ForegroundColorSpan(color.toArgb()), start, end)
    }
}

private fun Spannable.setBrush(
    brush: Brush?,
    alpha: Float,
    start: Int,
    end: Int
) {
    brush?.let {
        when (brush) {
            is SolidColor -> {
                setColor(brush.value, start, end)
            }
            is ShaderBrush -> {
                setSpan(
                    com.deadrudolph.uicomponents.view.textfield.core.paragraph.ShaderBrushSpan(
                        brush,
                        alpha
                    ), start, end)
            }
        }
    }
}

@OptIn(InternalPlatformTextApi::class)
internal fun Spannable.setTextDecoration(textDecoration: TextDecoration?, start: Int, end: Int) {
    textDecoration?.let {
        val textDecorationSpan = TextDecorationSpan(
            isUnderlineText = TextDecoration.Underline in it,
            isStrikethroughText = TextDecoration.LineThrough in it
        )
        setSpan(textDecorationSpan, start, end)
    }
}

@Suppress("DEPRECATION")
internal fun Spannable.setFontSize(fontSize: TextUnit, density: Density, start: Int, end: Int) {
    when (fontSize.type) {
        TextUnitType.Sp -> with(density) {
            setSpan(
                AbsoluteSizeSpan(/* size */ fontSize.toPx().roundToInt(), /* dip */ false),
                start,
                end
            )
        }
        TextUnitType.Em -> {
            setSpan(RelativeSizeSpan(fontSize.value), start, end)
        }
        else -> {
        } // Do nothing
    }
}

@OptIn(InternalPlatformTextApi::class)
private fun Spannable.setFontFeatureSettings(fontFeatureSettings: String?, start: Int, end: Int) {
    fontFeatureSettings?.let {
        setSpan(FontFeatureSpan(it), start, end)
    }
}

@OptIn(InternalPlatformTextApi::class)
private fun Spannable.setGeometricTransform(
    textGeometricTransform: TextGeometricTransform?,
    start: Int,
    end: Int
) {
    textGeometricTransform?.let {
        setSpan(ScaleXSpan(it.scaleX), start, end)
        setSpan(SkewXSpan(it.skewX), start, end)
    }
}

internal fun Spannable.setLocaleList(localeList: LocaleList?, start: Int, end: Int) {
    localeList?.let {
        setSpan(
            if (Build.VERSION.SDK_INT >= 24) {
                LocaleListHelperMethods.localeSpan(it)
            } else {
                val locale = if (it.isEmpty()) Locale.current else it[0]
                LocaleSpan(locale.toJavaLocale())
            },
            start,
            end
        )
    }
}

internal fun Spannable.setPlaceholders(
    placeholders: List<AnnotatedString.Range<Placeholder>>,
    density: Density
) {
    placeholders.fastForEach {
        val (placeholder, start, end) = it
        setPlaceholder(placeholder, start, end, density)
    }
}

@OptIn(InternalPlatformTextApi::class)
private fun Spannable.setPlaceholder(
    placeholder: Placeholder,
    start: Int,
    end: Int,
    density: Density
) {
    getSpans(start, end, EmojiSpan::class.java).forEach {
        removeSpan(it)
    }
    setSpan(
        with(placeholder) {
            PlaceholderSpan(
                width = width.value,
                widthUnit = width.spanUnit,
                height = height.value,
                heightUnit = height.spanUnit,
                pxPerSp = density.fontScale * density.density,
                verticalAlign = placeholderVerticalAlign.spanVerticalAlign
            )
        },
        start,
        end
    )
}

@OptIn(InternalPlatformTextApi::class)
@Suppress("DEPRECATION")
internal val TextUnit.spanUnit: Int
    get() = when (type) {
        TextUnitType.Sp -> PlaceholderSpan.UNIT_SP
        TextUnitType.Em -> PlaceholderSpan.UNIT_EM
        else -> PlaceholderSpan.UNIT_UNSPECIFIED
    }

@OptIn(InternalPlatformTextApi::class)
private val PlaceholderVerticalAlign.spanVerticalAlign: Int
    get() = when (this) {
        PlaceholderVerticalAlign.AboveBaseline -> PlaceholderSpan.ALIGN_ABOVE_BASELINE
        PlaceholderVerticalAlign.Top -> PlaceholderSpan.ALIGN_TOP
        PlaceholderVerticalAlign.Bottom -> PlaceholderSpan.ALIGN_BOTTOM
        PlaceholderVerticalAlign.Center -> PlaceholderSpan.ALIGN_CENTER
        PlaceholderVerticalAlign.TextTop -> PlaceholderSpan.ALIGN_TEXT_TOP
        PlaceholderVerticalAlign.TextBottom -> PlaceholderSpan.ALIGN_TEXT_BOTTOM
        PlaceholderVerticalAlign.TextCenter -> PlaceholderSpan.ALIGN_TEXT_CENTER
        else -> error("Invalid PlaceholderVerticalAlign")
    }

internal fun Spannable.setBackground(color: Color, start: Int, end: Int) {
    if (color.isSpecified) {
        setSpan(
            BackgroundColorSpan(color.toArgb()),
            start,
            end
        )
    }
}

@OptIn(InternalPlatformTextApi::class)
private fun Spannable.setShadow(shadow: Shadow?, start: Int, end: Int) {
    shadow?.let {
        setSpan(
            ShadowSpan(
                it.color.toArgb(),
                it.offset.x,
                it.offset.y,
                correctBlurRadius(it.blurRadius)
            ),
            start,
            end
        )
    }
}

@OptIn(InternalPlatformTextApi::class)
private fun Spannable.setDrawStyle(drawStyle: DrawStyle?, start: Int, end: Int) {
    drawStyle?.let {
        setSpan(DrawStyleSpan(it), start, end)
    }
}

internal fun flattenFontStylesAndApply(
    contextFontSpanStyle: SpanStyle?,
    spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    block: (SpanStyle?, Int, Int) -> Unit
) {
    // quick way out for single SpanStyle or empty list.
    if (spanStyles.size <= 1) {
        if (spanStyles.isNotEmpty()) {
            block(
                contextFontSpanStyle?.merge(spanStyles[0].item),
                spanStyles[0].start,
                spanStyles[0].end
            )
        }
        return
    }

    // Sort all span start and end points.
    // S1--S2--E1--S3--E3--E2
    val spanCount = spanStyles.size
    val transitionOffsets = Array(spanCount * 2) { 0 }
    spanStyles.fastForEachIndexed { idx, spanStyle ->
        transitionOffsets[idx] = spanStyle.start
        transitionOffsets[idx + spanCount] = spanStyle.end
    }
    transitionOffsets.sort()

    // S1--S2--E1--S3--E3--E2
    // - Go through all minimum intervals
    // - Find Spans that intersect with the given interval
    // - Merge all spans in order, starting from contextFontSpanStyle
    // - Apply the merged SpanStyle to the minimal interval
    var lastTransitionOffsets = transitionOffsets.first()
    for (transitionOffset in transitionOffsets) {
        // There might be duplicated transition offsets, we skip them here.
        if (transitionOffset == lastTransitionOffsets) {
            continue
        }

        // Check all spans that intersects with this transition range.
        var mergedSpanStyle = contextFontSpanStyle
        spanStyles.fastForEach { spanStyle ->
            // Empty spans do not intersect with anything, skip them.
            if (
                spanStyle.start != spanStyle.end &&
                intersect(
                    lastTransitionOffsets,
                    transitionOffset,
                    spanStyle.start,
                    spanStyle.end
                )
            ) {
                mergedSpanStyle = mergedSpanStyle?.merge(spanStyle.item)
            }
        }

        mergedSpanStyle?.let {
            block(it, lastTransitionOffsets, transitionOffset)
        }

        lastTransitionOffsets = transitionOffset
    }
}

internal val SpanStyle.needsLetterSpacingSpan: Boolean
    get() = letterSpacing.type == TextUnitType.Sp || letterSpacing.type == TextUnitType.Em

@OptIn(InternalPlatformTextApi::class)
@Suppress("DEPRECATION")
internal fun createLetterSpacingSpan(
    letterSpacing: TextUnit,
    density: Density
): MetricAffectingSpan? {
    return when (letterSpacing.type) {
        TextUnitType.Sp -> with(density) {
            LetterSpacingSpanPx(letterSpacing.toPx())
        }
        TextUnitType.Em -> {
            LetterSpacingSpanEm(letterSpacing.value)
        }
        else -> {
            null
        }
    }
}

internal fun TextStyle.hasFontAttributes(): Boolean {
    return toSpanStyle().hasFontAttributes() || fontSynthesis != null
}