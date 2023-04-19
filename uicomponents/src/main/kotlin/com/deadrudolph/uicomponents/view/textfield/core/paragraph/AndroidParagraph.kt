package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import android.os.Build
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import androidx.annotation.VisibleForTesting
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.asComposePath
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.android.InternalPlatformTextApi
import androidx.compose.ui.text.android.LayoutCompat
import androidx.compose.ui.text.android.TextLayout
import androidx.compose.ui.text.android.selection.WordBoundary
import androidx.compose.ui.text.android.style.IndentationFixSpan
import androidx.compose.ui.text.android.style.PlaceholderSpan
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.deadrudolph.uicomponents.view.textfield.core.range.TextRange
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.style.TextDecoration
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle
import com.deadrudolph.uicomponents.view.textfield.extension.isIncludeFontPaddingEnabled
import com.deadrudolph.uicomponents.view.textfield.extension.setSpan
import java.util.Locale

@OptIn(InternalPlatformTextApi::class, ExperimentalTextApi::class)
internal class AndroidParagraph(
    val paragraphIntrinsics: AndroidParagraphIntrinsics,
    val maxLines: Int,
    val ellipsis: Boolean,
    val constraints: Constraints
) : Paragraph {
    constructor(
        text: String,
        style: TextStyle,
        spanStyles: List<AnnotatedString.Range<SpanStyle>>,
        placeholders: List<AnnotatedString.Range<Placeholder>>,
        maxLines: Int,
        ellipsis: Boolean,
        constraints: Constraints,
        fontFamilyResolver: FontFamily.Resolver,
        density: Density
    ) : this(
        paragraphIntrinsics = AndroidParagraphIntrinsics(
            text = text,
            style = style,
            placeholders = placeholders,
            spanStyles = spanStyles,
            fontFamilyResolver = fontFamilyResolver,
            density = density
        ),
        maxLines = maxLines,
        ellipsis = ellipsis,
        constraints = constraints
    )

    private val layout: TextLayout

    @VisibleForTesting
    internal val charSequence: CharSequence

    init {
        require(constraints.minHeight == 0 && constraints.minWidth == 0) {
            "Setting Constraints.minWidth and Constraints.minHeight is not supported, " +
                    "these should be the default zero values instead."
        }
        require(maxLines >= 1) { "maxLines should be greater than 0" }

        val style = paragraphIntrinsics.style

        charSequence = if (shouldAttachIndentationFixSpan(style, ellipsis)) {
            // When letter spacing, align and ellipsize applied to text, the ellipsized line is
            // indented wrong. This function adds the IndentationFixSpan in order to fix the issue
            // with best effort. b/228463206
            paragraphIntrinsics.charSequence.attachIndentationFixSpan()
        } else {
            paragraphIntrinsics.charSequence
        }

        val alignment = toLayoutAlign(style.textAlign)

        val justificationMode = when (style.textAlign) {
            TextAlign.Justify -> LayoutCompat.JUSTIFICATION_MODE_INTER_WORD
            else -> LayoutCompat.DEFAULT_JUSTIFICATION_MODE
        }

        val hyphens = toLayoutHyphenationFrequency(style.hyphens)

        val breakStrategy = toLayoutBreakStrategy(style.lineBreak?.strategy)
        val lineBreakStyle = toLayoutLineBreakStyle(style.lineBreak?.strictness)
        val lineBreakWordStyle = toLayoutLineBreakWordStyle(style.lineBreak?.wordBreak)

        val ellipsize = if (ellipsis) {
            TextUtils.TruncateAt.END
        } else {
            null
        }

        val firstLayout = constructTextLayout(
            alignment = alignment,
            justificationMode = justificationMode,
            ellipsize = ellipsize,
            maxLines = maxLines,
            hyphens = hyphens,
            breakStrategy = breakStrategy,
            lineBreakStyle = lineBreakStyle,
            lineBreakWordStyle = lineBreakWordStyle
        )

        // Ellipsize if there's not enough vertical space to fit all lines
        if (ellipsis && firstLayout.height > constraints.maxHeight && maxLines > 1) {
            val calculatedMaxLines =
                firstLayout.numberOfLinesThatFitMaxHeight(constraints.maxHeight)
            layout = if (calculatedMaxLines >= 0 && calculatedMaxLines != maxLines) {
                constructTextLayout(
                    alignment = alignment,
                    justificationMode = justificationMode,
                    ellipsize = ellipsize,
                    // When we can't fully fit even a single line, measure with one line anyway.
                    // This will allow to have an ellipsis on that single line. If we measured with
                    // 0 maxLines, it would measure all lines with no ellipsis even though the first
                    // line might be partially visible
                    maxLines = calculatedMaxLines.coerceAtLeast(1),
                    hyphens = hyphens,
                    breakStrategy = breakStrategy,
                    lineBreakStyle = lineBreakStyle,
                    lineBreakWordStyle = lineBreakWordStyle
                )
            } else {
                firstLayout
            }
        } else {
            layout = firstLayout
        }

        // Brush is not fully realized on text until layout is complete and size information
        // is known. Brush can now be applied to the overall textpaint and all the spans.
        textPaint.setBrush(style.brush, Size(width, height), style.alpha)
        layout.getShaderBrushSpans().forEach { shaderBrushSpan ->
            shaderBrushSpan.size = Size(width, height)
        }
    }

    override val width: Float
        get() = constraints.maxWidth.toFloat()

    override val height: Float
        get() = layout.height.toFloat()

    override val maxIntrinsicWidth: Float
        get() = paragraphIntrinsics.maxIntrinsicWidth

    override val minIntrinsicWidth: Float
        get() = paragraphIntrinsics.minIntrinsicWidth

    override val firstBaseline: Float
        get() = getLineBaseline(0)

    override val lastBaseline: Float
        get() = getLineBaseline(lineCount - 1)

    override val didExceedMaxLines: Boolean
        get() = layout.didExceedMaxLines

    @VisibleForTesting
    internal val textLocale: Locale
        get() = paragraphIntrinsics.textPaint.textLocale

    /**
     * Resolved line count. If maxLines smaller than the real number of lines in the text, this
     * property will return the minimum between the two
     */
    override val lineCount: Int
        get() = layout.lineCount

    override val placeholderRects: List<Rect?> =
        with(charSequence) {
            if (this !is Spanned) return@with listOf()
            getSpans(0, length, PlaceholderSpan::class.java).map { span ->
                val start = getSpanStart(span)
                val end = getSpanEnd(span)
                // The line index of the PlaceholderSpan. In the case where PlaceholderSpan is
                // truncated due to maxLines limitation. It will return the index of last line.
                val line = layout.getLineForOffset(start)
                val exceedsMaxLines = line >= maxLines
                val isPlaceholderSpanEllipsized = layout.getLineEllipsisCount(line) > 0 &&
                        end > layout.getLineEllipsisOffset(line)
                val isPlaceholderSpanTruncated = end > layout.getLineEnd(line)
                // This Placeholder is ellipsized or truncated, return null instead.
                if (isPlaceholderSpanEllipsized || isPlaceholderSpanTruncated || exceedsMaxLines) {
                    return@map null
                }

                val direction = getBidiRunDirection(start)

                val left = when (direction) {
                    ResolvedTextDirection.Ltr ->
                        getHorizontalPosition(start, true)
                    ResolvedTextDirection.Rtl ->
                        getHorizontalPosition(start, true) - span.widthPx
                }
                val right = left + span.widthPx

                val top = with(layout) {
                    when (span.verticalAlign) {
                        PlaceholderSpan.ALIGN_ABOVE_BASELINE ->
                            getLineBaseline(line) - span.heightPx
                        PlaceholderSpan.ALIGN_TOP -> getLineTop(line)
                        PlaceholderSpan.ALIGN_BOTTOM -> getLineBottom(line) - span.heightPx
                        PlaceholderSpan.ALIGN_CENTER ->
                            (getLineTop(line) + getLineBottom(line) - span.heightPx) / 2
                        PlaceholderSpan.ALIGN_TEXT_TOP ->
                            span.fontMetrics.ascent + getLineBaseline(line)
                        PlaceholderSpan.ALIGN_TEXT_BOTTOM ->
                            span.fontMetrics.descent + getLineBaseline(line) - span.heightPx
                        PlaceholderSpan.ALIGN_TEXT_CENTER ->
                            with(span.fontMetrics) {
                                (ascent + descent - span.heightPx) / 2 + getLineBaseline(line)
                            }
                        else -> throw IllegalStateException("unexpected verticalAlignment")
                    }
                }

                val bottom = top + span.heightPx

                Rect(left, top, right, bottom)
            }
        }

    @VisibleForTesting
    internal val textPaint: AndroidTextPaint
        get() = paragraphIntrinsics.textPaint

    override fun getLineForVerticalPosition(vertical: Float): Int {
        return layout.getLineForVertical(vertical.toInt())
    }

    override fun getOffsetForPosition(position: Offset): Int {
        val line = layout.getLineForVertical(position.y.toInt())
        return layout.getOffsetForHorizontal(line, position.x)
    }

    /**
     * Returns the bounding box as Rect of the character for given character offset. Rect includes
     * the top, bottom, left and right of a character.
     */
    override fun getBoundingBox(offset: Int): Rect {
        val rectF = layout.getBoundingBox(offset)
        return with(rectF) { Rect(left = left, top = top, right = right, bottom = bottom) }
    }

    /**
     * Fills the bounding boxes for characters provided in the [range] into [array]. The array is
     * filled starting from [arrayStart] (inclusive). The coordinates are in local text layout
     * coordinates.
     *
     * The returned information consists of left/right of a character; line top and bottom for the
     * same character.
     *
     * For the grapheme consists of multiple code points, e.g. ligatures, combining marks, the first
     * character has the total width and the remaining are returned as zero-width.
     *
     * The array divided into segments of four where each index in that segment represents left,
     * top, right, bottom of the character.
     *
     * The size of the provided [array] should be greater or equal than the four times * [TextRange]
     * length.
     *
     * The final order of characters in the [array] is from [TextRange.min] to [TextRange.max].
     *
     * @param range the [TextRange] representing the start and end indices in the [Paragraph].
     * @param array the array to fill in the values. The array divided into segments of four where
     * each index in that segment represents left, top, right, bottom of the character.
     * @param arrayStart the inclusive start index in the array where the function will start
     * filling in the values from
     */
    fun fillBoundingBoxes(
        range: TextRange,
        array: FloatArray,
        arrayStart: Int
    ) {
        layout.fillBoundingBoxes(range.min, range.max, array, arrayStart)
    }

    override fun getPathForRange(start: Int, end: Int): Path {
        if (start !in 0..end || end > charSequence.length) {
            throw AssertionError(
                "Start($start) or End($end) is out of Range(0..${charSequence.length})," +
                        " or start > end!"
            )
        }
        val path = android.graphics.Path()
        layout.getSelectionPath(start, end, path)
        return path.asComposePath()
    }

    override fun getCursorRect(offset: Int): Rect {
        if (offset !in 0..charSequence.length) {
            throw AssertionError("offset($offset) is out of bounds (0,${charSequence.length}")
        }
        val horizontal = layout.getPrimaryHorizontal(offset)
        val line = layout.getLineForOffset(offset)

        // The width of the cursor is not taken into account. The callers of this API should use
        // rect.left to get the start X position and then adjust it according to the width if needed
        return Rect(
            horizontal,
            layout.getLineTop(line),
            horizontal,
            layout.getLineBottom(line)
        )
    }

    private val wordBoundary: WordBoundary by lazy(LazyThreadSafetyMode.NONE) {
        WordBoundary(textLocale, layout.text)
    }

    override fun getWordBoundary(offset: Int): TextRange {
        return TextRange(wordBoundary.getWordStart(offset), wordBoundary.getWordEnd(offset))
    }

    override fun getLineLeft(lineIndex: Int): Float = layout.getLineLeft(lineIndex)

    override fun getLineRight(lineIndex: Int): Float = layout.getLineRight(lineIndex)

    override fun getLineTop(lineIndex: Int): Float = layout.getLineTop(lineIndex)

    internal fun getLineAscent(lineIndex: Int): Float = layout.getLineAscent(lineIndex)

    internal fun getLineBaseline(lineIndex: Int): Float = layout.getLineBaseline(lineIndex)

    internal fun getLineDescent(lineIndex: Int): Float = layout.getLineDescent(lineIndex)

    override fun getLineBottom(lineIndex: Int): Float = layout.getLineBottom(lineIndex)

    override fun getLineHeight(lineIndex: Int): Float = layout.getLineHeight(lineIndex)

    override fun getLineWidth(lineIndex: Int): Float = layout.getLineWidth(lineIndex)

    override fun getLineStart(lineIndex: Int): Int = layout.getLineStart(lineIndex)

    override fun getLineEnd(lineIndex: Int, visibleEnd: Boolean): Int =
        if (visibleEnd) {
            layout.getLineVisibleEnd(lineIndex)
        } else {
            layout.getLineEnd(lineIndex)
        }

    override fun isLineEllipsized(lineIndex: Int): Boolean = layout.isLineEllipsized(lineIndex)

    override fun getLineForOffset(offset: Int): Int = layout.getLineForOffset(offset)

    override fun getHorizontalPosition(offset: Int, usePrimaryDirection: Boolean): Float =
        if (usePrimaryDirection) {
            layout.getPrimaryHorizontal(offset)
        } else {
            layout.getSecondaryHorizontal(offset)
        }

    override fun getParagraphDirection(offset: Int): ResolvedTextDirection {
        val lineIndex = layout.getLineForOffset(offset)
        val direction = layout.getParagraphDirection(lineIndex)
        return if (direction == 1) ResolvedTextDirection.Ltr else ResolvedTextDirection.Rtl
    }

    override fun getBidiRunDirection(offset: Int): ResolvedTextDirection {
        return if (layout.isRtlCharAt(offset))
            ResolvedTextDirection.Rtl
        else
            ResolvedTextDirection.Ltr
    }

    private fun TextLayout.getShaderBrushSpans(): Array<ShaderBrushSpan> {
        if (text !is Spanned) return emptyArray()
        val brushSpans = (text as Spanned).getSpans(
            0, text.length, ShaderBrushSpan::class.java
        )
        if (brushSpans.isEmpty()) return emptyArray()
        return brushSpans
    }

    override fun paint(
        canvas: Canvas,
        color: Color,
        shadow: Shadow?,
        textDecoration: TextDecoration?
    ) {
        with(textPaint) {
            setColor(color)
            setShadow(shadow)
            setTextDecoration(textDecoration)
        }

        paint(canvas)
    }

    @OptIn(ExperimentalTextApi::class)
    override fun paint(
        canvas: Canvas,
        color: Color,
        shadow: Shadow?,
        textDecoration: TextDecoration?,
        drawStyle: DrawStyle?,
        blendMode: BlendMode
    ) {
        val currBlendMode = textPaint.blendMode
        with(textPaint) {
            setColor(color)
            setShadow(shadow)
            setTextDecoration(textDecoration)
            setDrawStyle(drawStyle)
            this.blendMode = blendMode
        }

        paint(canvas)

        textPaint.blendMode = currBlendMode
    }

    @OptIn(ExperimentalTextApi::class)
    override fun paint(
        canvas: Canvas,
        brush: Brush,
        alpha: Float,
        shadow: Shadow?,
        textDecoration: TextDecoration?,
        drawStyle: DrawStyle?,
        blendMode: BlendMode
    ) {
        val currBlendMode = textPaint.blendMode
        with(textPaint) {
            setBrush(brush, Size(width, height), alpha)
            setShadow(shadow)
            setTextDecoration(textDecoration)
            setDrawStyle(drawStyle)
            this.blendMode = blendMode
        }

        paint(canvas)

        textPaint.blendMode = currBlendMode
    }

    private fun paint(canvas: Canvas) {
        val nativeCanvas = canvas.nativeCanvas
        if (didExceedMaxLines) {
            nativeCanvas.save()
            nativeCanvas.clipRect(0f, 0f, width, height)
        }
        layout.paint(nativeCanvas)
        if (didExceedMaxLines) {
            nativeCanvas.restore()
        }
    }

    private fun constructTextLayout(
        alignment: Int,
        justificationMode: Int,
        ellipsize: TextUtils.TruncateAt?,
        maxLines: Int,
        hyphens: Int,
        breakStrategy: Int,
        lineBreakStyle: Int,
        lineBreakWordStyle: Int
    ) =
        TextLayout(
            charSequence = charSequence,
            width = width,
            textPaint = textPaint,
            ellipsize = ellipsize,
            alignment = alignment,
            textDirectionHeuristic = paragraphIntrinsics.textDirectionHeuristic,
            lineSpacingMultiplier = LayoutCompat.DEFAULT_LINESPACING_MULTIPLIER,
            maxLines = maxLines,
            justificationMode = justificationMode,
            layoutIntrinsics = paragraphIntrinsics.layoutIntrinsics,
            includePadding = paragraphIntrinsics.style.isIncludeFontPaddingEnabled(),
            fallbackLineSpacing = true,
            hyphenationFrequency = hyphens,
            breakStrategy = breakStrategy,
            lineBreakStyle = lineBreakStyle,
            lineBreakWordStyle = lineBreakWordStyle
        )
}

/**
 * Converts [TextAlign] into [TextLayout] alignment constants.
 */
@OptIn(InternalPlatformTextApi::class)
private fun toLayoutAlign(align: TextAlign?): Int = when (align) {
    TextAlign.Left -> LayoutCompat.ALIGN_LEFT
    TextAlign.Right -> LayoutCompat.ALIGN_RIGHT
    TextAlign.Center -> LayoutCompat.ALIGN_CENTER
    TextAlign.Start -> LayoutCompat.ALIGN_NORMAL
    TextAlign.End -> LayoutCompat.ALIGN_OPPOSITE
    else -> LayoutCompat.DEFAULT_ALIGNMENT
}

@OptIn(ExperimentalTextApi::class, InternalPlatformTextApi::class)
private fun toLayoutHyphenationFrequency(hyphens: Hyphens?): Int = when (hyphens) {
    Hyphens.Auto -> if (Build.VERSION.SDK_INT <= 32) {
        LayoutCompat.HYPHENATION_FREQUENCY_NORMAL
    } else {
        LayoutCompat.HYPHENATION_FREQUENCY_NORMAL_FAST
    }
    Hyphens.None -> LayoutCompat.HYPHENATION_FREQUENCY_NONE
    else -> LayoutCompat.HYPHENATION_FREQUENCY_NONE
}

@OptIn(ExperimentalTextApi::class, InternalPlatformTextApi::class)
private fun toLayoutBreakStrategy(breakStrategy: LineBreak.Strategy?): Int = when (breakStrategy) {
    LineBreak.Strategy.Simple -> LayoutCompat.BREAK_STRATEGY_SIMPLE
    LineBreak.Strategy.HighQuality -> LayoutCompat.BREAK_STRATEGY_HIGH_QUALITY
    LineBreak.Strategy.Balanced -> LayoutCompat.BREAK_STRATEGY_BALANCED
    else -> LayoutCompat.BREAK_STRATEGY_SIMPLE
}

@OptIn(ExperimentalTextApi::class, InternalPlatformTextApi::class)
private fun toLayoutLineBreakStyle(lineBreakStrictness: LineBreak.Strictness?): Int =
    when (lineBreakStrictness) {
        LineBreak.Strictness.Default -> LayoutCompat.LINE_BREAK_STYLE_NONE
        LineBreak.Strictness.Loose -> LayoutCompat.LINE_BREAK_STYLE_LOOSE
        LineBreak.Strictness.Normal -> LayoutCompat.LINE_BREAK_STYLE_NORMAL
        LineBreak.Strictness.Strict -> LayoutCompat.LINE_BREAK_STYLE_STRICT
        else -> LayoutCompat.LINE_BREAK_STYLE_NONE
    }

@OptIn(ExperimentalTextApi::class, InternalPlatformTextApi::class)
private fun toLayoutLineBreakWordStyle(lineBreakWordStyle: LineBreak.WordBreak?): Int =
    when (lineBreakWordStyle) {
        LineBreak.WordBreak.Default -> LayoutCompat.LINE_BREAK_WORD_STYLE_NONE
        LineBreak.WordBreak.Phrase -> LayoutCompat.LINE_BREAK_WORD_STYLE_PHRASE
        else -> LayoutCompat.LINE_BREAK_WORD_STYLE_NONE
    }

@OptIn(InternalPlatformTextApi::class)
private fun TextLayout.numberOfLinesThatFitMaxHeight(maxHeight: Int): Int {
    for (lineIndex in 0 until lineCount) {
        if (getLineBottom(lineIndex) > maxHeight) return lineIndex
    }
    return lineCount
}

private fun shouldAttachIndentationFixSpan(textStyle: TextStyle, ellipsis: Boolean) =
    with(textStyle) {
        ellipsis && (letterSpacing != 0.sp && letterSpacing != TextUnit.Unspecified) &&
                (textAlign != null && textAlign != TextAlign.Start && textAlign != TextAlign.Justify)
    }

@OptIn(InternalPlatformTextApi::class)
private fun CharSequence.attachIndentationFixSpan(): CharSequence {
    if (isEmpty()) return this
    val spannable = if (this is Spannable) this else SpannableString(this)
    spannable.setSpan(IndentationFixSpan(), spannable.length - 1, spannable.length - 1)
    return spannable

}