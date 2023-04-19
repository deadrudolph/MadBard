package com.deadrudolph.uicomponents.view.textfield.core.text_field

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontSynthesis
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle
import com.deadrudolph.uicomponents.view.textfield.core.style.resolveDefaults

@Suppress("ModifierInspectorInfo")
internal fun Modifier.textFieldMinSize(style: TextStyle) = composed {
    val density = LocalDensity.current
    val fontFamilyResolver = LocalFontFamilyResolver.current
    val layoutDirection = LocalLayoutDirection.current

    val resolvedStyle = remember(style, layoutDirection) {
        androidx.compose.ui.text.input.TextFieldValue
        resolveDefaults(style, layoutDirection)
    }
    val typeface by remember(fontFamilyResolver, resolvedStyle) {
        fontFamilyResolver.resolve(
            resolvedStyle.fontFamily,
            resolvedStyle.fontWeight ?: FontWeight.Normal,
            resolvedStyle.fontStyle ?: FontStyle.Normal,
            resolvedStyle.fontSynthesis ?: FontSynthesis.All
        )
    }

    val minSizeState = remember {
        TextFieldSize(layoutDirection, density, fontFamilyResolver, style, typeface)
    }

    minSizeState.update(layoutDirection, density, fontFamilyResolver, resolvedStyle, typeface)

    Modifier.layout { measurable, constraints ->
        Modifier.defaultMinSize()
        val minSize = minSizeState.minSize

        val childConstraints = constraints.copy(
            minWidth = minSize.width.coerceIn(constraints.minWidth, constraints.maxWidth),
            minHeight = minSize.height.coerceIn(constraints.minHeight, constraints.maxHeight)
        )
        val measured = measurable.measure(childConstraints)
        layout(measured.width, measured.height) {
            measured.placeRelative(0, 0)
        }
    }
}

internal class TextFieldSize(
    var layoutDirection: LayoutDirection,
    var density: Density,
    var fontFamilyResolver: FontFamily.Resolver,
    var resolvedStyle: TextStyle,
    var typeface: Any
) {
    var minSize = computeMinSize()
        private set

    fun update(
        layoutDirection: LayoutDirection,
        density: Density,
        fontFamilyResolver: FontFamily.Resolver,
        resolvedStyle: TextStyle,
        typeface: Any
    ) {
        if (layoutDirection != this.layoutDirection ||
            density != this.density ||
            fontFamilyResolver != this.fontFamilyResolver ||
            resolvedStyle != this.resolvedStyle ||
            typeface != this.typeface
        ) {
            this.layoutDirection = layoutDirection
            this.density = density
            this.fontFamilyResolver = fontFamilyResolver
            this.resolvedStyle = resolvedStyle
            this.typeface = typeface
            minSize = computeMinSize()
        }
    }

    private fun computeMinSize(): IntSize {
        return computeSizeForDefaultText(
            style = resolvedStyle,
            density = density,
            fontFamilyResolver = fontFamilyResolver
        )
    }
}