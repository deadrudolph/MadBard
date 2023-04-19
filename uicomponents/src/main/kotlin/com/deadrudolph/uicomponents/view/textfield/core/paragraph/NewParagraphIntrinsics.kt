package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle

interface NewParagraphIntrinsics {
    /**
     * The width for text if all soft wrap opportunities were taken.
     */
    val minIntrinsicWidth: Float

    /**
     * Returns the smallest width beyond which increasing the width never
     * decreases the height.
     */
    val maxIntrinsicWidth: Float

    /**
     * Any [Paragraph] rendered using this [ParagraphIntrinsics] will be measured and drawn using
     * stale resolved fonts.
     *
     * If this is false, this [ParagraphIntrinsics] is using the most current font resolution from
     * [FontFamily.Resolver].
     *
     * If this is true, recreating this [ParagraphIntrinsics] will use new fonts from
     * [FontFamily.Resolver] for both display and measurement. Recreating this [ParagraphIntrinsics]
     * and displaying the resulting [Paragraph] causes user-visible reflow of the displayed text.
     *
     * Once true, this will never become false without recreating this [ParagraphIntrinsics].
     *
     * It is discouraged, but safe, to continue to use this object after this becomes true. The
     * only impact of using this object after [hasStaleResolvedFonts] becomes true is stale
     * resolutions of async fonts for measurement and display.
     */
    val hasStaleResolvedFonts: Boolean
        get() = false
}

/**
 *  Factory method to create a [ParagraphIntrinsics].
 *
 *  If the [style] does not contain any [androidx.compose.ui.text.style.TextDirection],
 * [androidx.compose.ui.text.style.TextDirection.Content] is used as the default value.
 *
 * @see ParagraphIntrinsics
 */
@Suppress("DEPRECATION")
@Deprecated(
    "Font.ResourceLoader is deprecated, instead use FontFamily.Resolver",
    ReplaceWith("ParagraphIntrinsics(text, style, spanStyles, placeholders, density, " +
            "fontFamilyResolver")
)

internal fun ParagraphIntrinsics(
    text: String,
    style: TextStyle,
    spanStyles: List<AnnotatedString.Range<SpanStyle>> = listOf(),
    placeholders: List<AnnotatedString.Range<Placeholder>> = listOf(),
    density: Density,
    fontFamilyResolver: FontFamily.Resolver
): NewParagraphIntrinsics = ActualParagraphIntrinsics(
    text = text,
    style = style,
    spanStyles = spanStyles,
    placeholders = placeholders,
    density = density,
    fontFamilyResolver = fontFamilyResolver
)