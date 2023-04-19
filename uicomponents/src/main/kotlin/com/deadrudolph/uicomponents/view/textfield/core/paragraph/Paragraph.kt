package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.ResolvedTextDirection
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import com.deadrudolph.uicomponents.view.textfield.core.constants.DefaultMaxLines
import com.deadrudolph.uicomponents.view.textfield.core.range.TextRange
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.style.TextDecoration
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle

sealed interface Paragraph {
    val width: Float
    val height: Float
    val minIntrinsicWidth: Float
    val maxIntrinsicWidth: Float
    val firstBaseline: Float
    val lastBaseline: Float
    val didExceedMaxLines: Boolean
    val lineCount: Int
    val placeholderRects: List<Rect?>
    fun getPathForRange(start: Int, end: Int): Path
    fun getCursorRect(offset: Int): Rect
    fun getLineLeft(lineIndex: Int): Float
    fun getLineRight(lineIndex: Int): Float
    fun getLineTop(lineIndex: Int): Float
    fun getLineBottom(lineIndex: Int): Float
    fun getLineHeight(lineIndex: Int): Float
    fun getLineWidth(lineIndex: Int): Float
    fun getLineStart(lineIndex: Int): Int
    fun getLineEnd(lineIndex: Int, visibleEnd: Boolean): Int
    fun isLineEllipsized(lineIndex: Int): Boolean
    fun getLineForOffset(offset: Int): Int
    fun getHorizontalPosition(offset: Int, usePrimaryDirection: Boolean): Float
    fun getParagraphDirection(offset: Int): ResolvedTextDirection
    fun getBidiRunDirection(offset: Int): ResolvedTextDirection
    fun getLineForVerticalPosition(vertical: Float): Int
    fun getOffsetForPosition(position: Offset): Int
    fun getBoundingBox(offset: Int): Rect
    fun getWordBoundary(offset: Int): TextRange
    fun paint(canvas: Canvas, color: Color, shadow: Shadow?, textDecoration: TextDecoration?)

    @ExperimentalTextApi
    fun paint(
        canvas: Canvas,
        color: Color,
        shadow: Shadow?,
        textDecoration: TextDecoration?,
        drawStyle: DrawStyle?,
        blendMode: BlendMode
    )

    @ExperimentalTextApi
    fun paint(
        canvas: Canvas,
        brush: Brush,
        alpha: Float,
        shadow: Shadow?,
        textDecoration: TextDecoration?,
        drawStyle: DrawStyle?,
        blendMode: BlendMode
    )
}

internal fun Paragraph(
    text: String,
    style: TextStyle,
    constraints: Constraints,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver,
    spanStyles: List<AnnotatedString.Range<SpanStyle>> = listOf(),
    placeholders: List<AnnotatedString.Range<Placeholder>> = listOf(),
    maxLines: Int = DefaultMaxLines,
    ellipsis: Boolean = false
): Paragraph = ActualParagraph(
    text,
    style,
    spanStyles,
    placeholders,
    maxLines,
    ellipsis,
    constraints,
    density,
    fontFamilyResolver
)

internal fun ActualParagraph(
    text: String,
    style: TextStyle,
    spanStyles: List<AnnotatedString.Range<SpanStyle>>,
    placeholders: List<AnnotatedString.Range<Placeholder>>,
    maxLines: Int,
    ellipsis: Boolean,
    constraints: Constraints,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver
): Paragraph = AndroidParagraph(
    AndroidParagraphIntrinsics(
        text = text,
        style = style,
        placeholders = placeholders,
        spanStyles = spanStyles,
        fontFamilyResolver = fontFamilyResolver,
        density = density
    ),
    maxLines,
    ellipsis,
    constraints
)

fun Paragraph(
    paragraphIntrinsics: NewParagraphIntrinsics,
    constraints: Constraints,
    maxLines: Int = DefaultMaxLines,
    ellipsis: Boolean = false
): Paragraph = ActualParagraph(
    paragraphIntrinsics,
    maxLines,
    ellipsis,
    constraints
)

internal fun ActualParagraph(
    paragraphIntrinsics: NewParagraphIntrinsics,
    maxLines: Int,
    ellipsis: Boolean,
    constraints: Constraints
): Paragraph = AndroidParagraph(
    paragraphIntrinsics as AndroidParagraphIntrinsics,
    maxLines,
    ellipsis,
    constraints
)