package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.isUnspecified
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import com.deadrudolph.uicomponents.view.textfield.core.span.modulate
import com.deadrudolph.uicomponents.view.textfield.core.style.TextDecoration
import kotlin.math.ceil
import kotlin.math.roundToInt

internal val DefaultTextBlendMode = BlendMode.SrcOver

object TextPainter {

    // TODO(b/236964276): Deprecate when TextMeasurer and drawText are no longer Experimental
    /**
     * Paints the text onto the given canvas.
     *
     * @param canvas a canvas to be drawn
     * @param textLayoutResult a result of text layout
     */
    @OptIn(ExperimentalTextApi::class)
    fun paint(canvas: Canvas, textLayoutResult: NewTextLayoutResult) {
        val needClipping = textLayoutResult.hasVisualOverflow &&
                textLayoutResult.layoutInput.overflow != TextOverflow.Visible
        if (needClipping) {
            val width = textLayoutResult.size.width.toFloat()
            val height = textLayoutResult.size.height.toFloat()
            val bounds = Rect(Offset.Zero, Size(width, height))
            canvas.save()
            canvas.clipRect(bounds)
        }

        /* inline resolveSpanStyleDefaults to avoid an allocation in draw */
        val style = textLayoutResult.layoutInput.style.toSpanStyle()
        val textDecoration = style.textDecoration ?: TextDecoration.None
        val shadow = style.shadow ?: Shadow.None
        val drawStyle = style.drawStyle ?: Fill
        try {
            val brush = style.brush
            if (brush != null) {
                val alpha = if (style.alpha.isNaN()) {
                    1.0f
                } else {
                    style.alpha
                }
                textLayoutResult.multiParagraph.paint(
                    canvas = canvas,
                    brush = brush,
                    alpha = alpha,
                    shadow = shadow,
                    decoration = textDecoration,
                    drawStyle = drawStyle
                )
            } else {
                val color = if (style.color.isUnspecified) {
                    Color.Black
                } else {
                    style.color
                }
                textLayoutResult.multiParagraph.paint(
                    canvas = canvas,
                    color = color,
                    shadow = shadow,
                    decoration = textDecoration,
                    drawStyle = drawStyle
                )
            }
        } finally {
            if (needClipping) {
                canvas.restore()
            }
        }
    }
}

/**
 * Draw an existing text layout as produced by [TextMeasurer].
 *
 * This draw function cannot relayout when async font loading resolves. If using async fonts or
 * other dynamic text layout, you are responsible for invalidating layout on changes.
 *
 * @param textLayoutResult Text Layout to be drawn
 * @param color Text color to use
 * @param topLeft Offsets the text from top left point of the current coordinate system.
 * @param alpha opacity to be applied to the [color] from 0.0f to 1.0f representing fully
 * transparent to fully opaque respectively
 * @param shadow The shadow effect applied on the text.
 * @param textDecoration The decorations to paint on the text (e.g., an underline).
 * @param drawStyle Whether or not the text is stroked or filled in.
 * @param blendMode Blending algorithm to be applied to the text
 *
 * @sample androidx.compose.ui.text.samples.DrawTextLayoutResultSample
 */
@ExperimentalTextApi
fun DrawScope.drawText(
    textLayoutResult: NewTextLayoutResult,
    color: Color = Color.Unspecified,
    topLeft: Offset = Offset.Zero,
    alpha: Float = Float.NaN,
    shadow: Shadow? = null,
    textDecoration: TextDecoration? = null,
    drawStyle: DrawStyle? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode
) {
    val newShadow = shadow ?: textLayoutResult.layoutInput.style.shadow
    val newTextDecoration = textDecoration ?: textLayoutResult.layoutInput.style.textDecoration
    val newDrawStyle = drawStyle ?: textLayoutResult.layoutInput.style.drawStyle

    withTransform({
        translate(topLeft.x, topLeft.y)
        clip(textLayoutResult)
    }) {
        // if text layout was created using brush, and [color] is unspecified, we should treat this
        // like drawText(brush) call
        val brush = textLayoutResult.layoutInput.style.brush
        if (brush != null && color.isUnspecified) {
            textLayoutResult.multiParagraph.paint(
                drawContext.canvas,
                brush,
                if (!alpha.isNaN()) alpha else textLayoutResult.layoutInput.style.alpha,
                newShadow,
                newTextDecoration,
                newDrawStyle,
                blendMode
            )
        } else {
            textLayoutResult.multiParagraph.paint(
                drawContext.canvas,
                color.takeOrElse { textLayoutResult.layoutInput.style.color }.modulate(alpha),
                newShadow,
                newTextDecoration,
                newDrawStyle,
                blendMode
            )
        }
    }
}

/**
 * Draw an existing text layout as produced by [TextMeasurer].
 *
 * This draw function cannot relayout when async font loading resolves. If using async fonts or
 * other dynamic text layout, you are responsible for invalidating layout on changes.
 *
 * @param textLayoutResult Text Layout to be drawn
 * @param brush The brush to use when drawing the text.
 * @param topLeft Offsets the text from top left point of the current coordinate system.
 * @param alpha Opacity to be applied to [brush] from 0.0f to 1.0f representing fully
 * transparent to fully opaque respectively.
 * @param shadow The shadow effect applied on the text.
 * @param textDecoration The decorations to paint on the text (e.g., an underline).
 * @param drawStyle Whether or not the text is stroked or filled in.
 * @param blendMode Blending algorithm to be applied to the text
 *
 * @sample androidx.compose.ui.text.samples.DrawTextLayoutResultSample
 */
@ExperimentalTextApi
fun DrawScope.drawText(
    textLayoutResult: NewTextLayoutResult,
    brush: Brush,
    topLeft: Offset = Offset.Zero,
    alpha: Float = Float.NaN,
    shadow: Shadow? = null,
    textDecoration: TextDecoration? = null,
    drawStyle: DrawStyle? = null,
    blendMode: BlendMode = DrawScope.DefaultBlendMode
) {
    val newShadow = shadow ?: textLayoutResult.layoutInput.style.shadow
    val newTextDecoration = textDecoration ?: textLayoutResult.layoutInput.style.textDecoration
    val newDrawStyle = drawStyle ?: textLayoutResult.layoutInput.style.drawStyle

    withTransform({
        translate(topLeft.x, topLeft.y)
        clip(textLayoutResult)
    }) {
        textLayoutResult.multiParagraph.paint(
            drawContext.canvas,
            brush,
            if (!alpha.isNaN()) alpha else textLayoutResult.layoutInput.style.alpha,
            newShadow,
            newTextDecoration,
            newDrawStyle,
            blendMode
        )
    }
}

private fun DrawTransform.clip(textLayoutResult: NewTextLayoutResult) {
    if (textLayoutResult.hasVisualOverflow &&
        textLayoutResult.layoutInput.overflow != TextOverflow.Visible
    ) {
        clipRect(
            left = 0f,
            top = 0f,
            right = textLayoutResult.size.width.toFloat(),
            bottom = textLayoutResult.size.height.toFloat()
        )
    }
}

/**
 * Converts given size and placement preferences to Constraints for measuring text layout.
 */
private fun DrawScope.textLayoutConstraints(
    size: Size,
    topLeft: Offset
): Constraints {
    val minWidth: Int
    val maxWidth: Int
    val isWidthNaN = size.isUnspecified || size.width.isNaN()
    if (isWidthNaN) {
        minWidth = 0
        maxWidth = ceil(this.size.width - topLeft.x).roundToInt()
    } else {
        val fixedWidth = ceil(size.width).roundToInt()
        minWidth = fixedWidth
        maxWidth = fixedWidth
    }

    val minHeight: Int
    val maxHeight: Int
    val isHeightNaN = size.isUnspecified || size.height.isNaN()
    if (isHeightNaN) {
        minHeight = 0
        maxHeight = ceil(this.size.height - topLeft.y).roundToInt()
    } else {
        val fixedHeight = ceil(size.height).roundToInt()
        minHeight = fixedHeight
        maxHeight = fixedHeight
    }

    return Constraints(minWidth, maxWidth, minHeight, maxHeight)
}