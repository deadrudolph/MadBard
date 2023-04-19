package com.deadrudolph.uicomponents.view.textfield.core.span

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.isSpecified
import com.deadrudolph.uicomponents.view.textfield.extension.lerp

interface TextForegroundStyle {
    val color: Color

    val brush: Brush?

    val alpha: Float

    fun merge(other: TextForegroundStyle): TextForegroundStyle {
        // This control prevents Color or Unspecified TextForegroundStyle to override an existing
        // Brush. It is a temporary measure to prevent Material Text composables to remove given
        // Brush from a TextStyle.
        // TODO(b/230787077): Just return other.takeOrElse { this } when Brush is stable.
        return when {
            other is BrushStyle && this is BrushStyle ->
                BrushStyle(other.value, other.alpha.takeOrElse { this.alpha })
            other is BrushStyle && this !is BrushStyle -> other
            other !is BrushStyle && this is BrushStyle -> this
            else -> other.takeOrElse { this }
        }
    }

    fun takeOrElse(other: () -> TextForegroundStyle): TextForegroundStyle {
        return if (this != Unspecified) this else other()
    }

    object Unspecified : TextForegroundStyle {
        override val color: Color
            get() = Color.Unspecified

        override val brush: Brush?
            get() = null

        override val alpha: Float
            get() = Float.NaN
    }

    companion object {
        fun from(color: Color): TextForegroundStyle {
            return if (color.isSpecified) ColorStyle(color) else Unspecified
        }

        fun from(brush: Brush?, alpha: Float): TextForegroundStyle {
            return when (brush) {
                null -> Unspecified
                is SolidColor -> from(brush.value.modulate(alpha))
                is ShaderBrush -> BrushStyle(brush, alpha)
            }
        }
    }
}

private data class ColorStyle(
    val value: Color
) : TextForegroundStyle {
    init {
        require(value.isSpecified) {
            "ColorStyle value must be specified, use TextForegroundStyle.Unspecified instead."
        }
    }

    override val color: Color
        get() = value

    override val brush: Brush?
        get() = null

    override val alpha: Float
        get() = color.alpha
}

data class BrushStyle(
    val value: ShaderBrush,
    override val alpha: Float
) : TextForegroundStyle {
    override val color: Color
        get() = Color.Unspecified

    override val brush: Brush
        get() = value
}

/**
 * If both TextForegroundStyles do not represent a Brush, lerp the color values. Otherwise, lerp
 * start to end discretely.
 */
internal fun lerp(
    start: TextForegroundStyle,
    stop: TextForegroundStyle,
    fraction: Float
): TextForegroundStyle {
    return if ((start !is BrushStyle && stop !is BrushStyle)) {
        TextForegroundStyle.from(
            androidx.compose.ui.graphics.lerp(
                start.color,
                stop.color,
                fraction
            )
        )
    } else if (start is BrushStyle && stop is BrushStyle) {
        TextForegroundStyle.from(
            lerpDiscrete(start.brush, stop.brush, fraction),
            lerp(start.alpha, stop.alpha, fraction)
        )
    } else {
        lerpDiscrete(start, stop, fraction)
    }
}

internal fun Color.modulate(alpha: Float): Color = when {
    alpha.isNaN() || alpha >= 1f -> this
    else -> this.copy(alpha = this.alpha * alpha)
}

private fun Float.takeOrElse(block: () -> Float): Float {
    return if (this.isNaN()) block() else this
}