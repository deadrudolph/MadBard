package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.text.InternalFoundationTextApi
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.deadrudolph.uicomponents.view.textfield.core.paragraph.MultiParagraph
import com.deadrudolph.uicomponents.view.textfield.core.paragraph.MultiParagraphIntrinsics
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle
import com.deadrudolph.uicomponents.view.textfield.core.style.resolveDefaults
import com.deadrudolph.uicomponents.view.textfield.extension.canReuse
import com.deadrudolph.uicomponents.view.textfield.extension.ceilToIntPx

@InternalFoundationTextApi // Used by benchmarks
@Stable
internal class TextDelegate(
    val text: AnnotatedString,
    val style: TextStyle,
    val maxLines: Int = Int.MAX_VALUE,
    val minLines: Int = DefaultMinLines,
    val softWrap: Boolean = true,
    val overflow: TextOverflow = TextOverflow.Clip,
    val density: Density,
    val fontFamilyResolver: FontFamily.Resolver,
    val placeholders: List<AnnotatedString.Range<Placeholder>> = emptyList()
) {
    /*@VisibleForTesting*/
    // NOTE(text-perf-review): it seems like TextDelegate essentially guarantees that we use
    // MultiParagraph. Can we have a fast-path that uses just Paragraph in simpler cases (ie,
    // String)?
    internal var paragraphIntrinsics: MultiParagraphIntrinsics? = null
    internal var intrinsicsLayoutDirection: LayoutDirection? = null

    private val nonNullIntrinsics: MultiParagraphIntrinsics
        get() = paragraphIntrinsics
        ?: throw IllegalStateException("layoutIntrinsics must be called first")

    /**
     * The width for text if all soft wrap opportunities were taken.
     *
     * Valid only after [layout] has been called.
     */
    val minIntrinsicWidth: Int get() = nonNullIntrinsics.minIntrinsicWidth.ceilToIntPx()

    /**
     * The width at which increasing the width of the text no longer decreases the height.
     *
     * Valid only after [layout] has been called.
     */
    val maxIntrinsicWidth: Int get() = nonNullIntrinsics.maxIntrinsicWidth.ceilToIntPx()

    init {
        check(maxLines > 0)
        check(minLines > 0)
        check(minLines <= maxLines)
    }

    fun layoutIntrinsics(layoutDirection: LayoutDirection) {
        val localIntrinsics = paragraphIntrinsics
        val intrinsics = if (
            localIntrinsics == null ||
            layoutDirection != intrinsicsLayoutDirection ||
            localIntrinsics.hasStaleResolvedFonts
        ) {
            intrinsicsLayoutDirection = layoutDirection
            MultiParagraphIntrinsics(
                annotatedString = text,
                style = resolveDefaults(style, layoutDirection),
                density = density,
                fontFamilyResolver = fontFamilyResolver,
                placeholders = placeholders
            )
        } else {
            localIntrinsics
        }

        paragraphIntrinsics = intrinsics
    }

    /**
     * Computes the visual position of the glyphs for painting the text.
     *
     * The text will layout with a width that's as close to its max intrinsic width as possible
     * while still being greater than or equal to `minWidth` and less than or equal to `maxWidth`.
     */
    private fun layoutText(
        constraints: Constraints,
        layoutDirection: LayoutDirection
    ): MultiParagraph {
        layoutIntrinsics(layoutDirection)

        val minWidth = constraints.minWidth
        val widthMatters = softWrap || overflow == TextOverflow.Ellipsis
        val maxWidth = if (widthMatters && constraints.hasBoundedWidth) {
            constraints.maxWidth
        } else {
            Constraints.Infinity
        }

        // This is a fallback behavior because native text layout doesn't support multiple
        // ellipsis in one text layout.
        // When softWrap is turned off and overflow is ellipsis, it's expected that each line
        // that exceeds maxWidth will be ellipsized.
        // For example,
        // input text:
        //     "AAAA\nAAAA"
        // maxWidth:
        //     3 * fontSize that only allow 3 characters to be displayed each line.
        // expected output:
        //     AA…
        //     AA…
        // Here we assume there won't be any '\n' character when softWrap is false. And make
        // maxLines 1 to implement the similar behavior.
        val overwriteMaxLines = !softWrap && overflow == TextOverflow.Ellipsis
        val finalMaxLines = if (overwriteMaxLines) 1 else maxLines

        // if minWidth == maxWidth the width is fixed.
        //    therefore we can pass that value to our paragraph and use it
        // if minWidth != maxWidth there is a range
        //    then we should check if the max intrinsic width is in this range to decide the
        //    width to be passed to Paragraph
        //        if max intrinsic width is between minWidth and maxWidth
        //           we can use it to layout
        //        else if max intrinsic width is greater than maxWidth, we can only use maxWidth
        //        else if max intrinsic width is less than minWidth, we should use minWidth
        val width = if (minWidth == maxWidth) {
            maxWidth
        } else {
            maxIntrinsicWidth.coerceIn(minWidth, maxWidth)
        }

        return MultiParagraph(
            intrinsics = nonNullIntrinsics,
            constraints = Constraints(maxWidth = width, maxHeight = constraints.maxHeight),
            // This is a fallback behavior for ellipsis. Native
            maxLines = finalMaxLines,
            ellipsis = overflow == TextOverflow.Ellipsis
        )
    }

    fun layout(
        constraints: Constraints,
        layoutDirection: LayoutDirection,
        prevResult: NewTextLayoutResult? = null
    ): NewTextLayoutResult {
        if (prevResult != null && prevResult.canReuse(
                text, style, placeholders, maxLines, softWrap, overflow, density, layoutDirection,
                fontFamilyResolver, constraints
            )
        ) {
            // NOTE(text-perf-review): seems like there's a nontrivial chance for us to be able
            // to just return prevResult here directly?
            return with(prevResult) {
                copy(
                    layoutInput = TextLayoutInput(
                        layoutInput.text,
                        style,
                        layoutInput.placeholders,
                        layoutInput.maxLines,
                        layoutInput.softWrap,
                        layoutInput.overflow,
                        layoutInput.density,
                        layoutInput.layoutDirection,
                        layoutInput.fontFamilyResolver,
                        constraints
                    ),
                    size = constraints.constrain(
                        IntSize(
                            multiParagraph.width.ceilToIntPx(),
                            multiParagraph.height.ceilToIntPx()
                        )
                    )
                )
            }
        }

        val multiParagraph = layoutText(
            constraints,
            layoutDirection
        )

        val size = constraints.constrain(
            IntSize(
                multiParagraph.width.ceilToIntPx(),
                multiParagraph.height.ceilToIntPx()
            )
        )

        // NOTE(text-perf-review): it feels odd to create the input + result at the same time. if
        // the allocation of these objects is 1:1 then it might make sense to just merge them?
        // Alternatively, we might be able to save some effort here by having a common object for
        // the types that go into the result here that are less likely to change
        return NewTextLayoutResult(
            TextLayoutInput(
                text,
                style,
                placeholders,
                maxLines,
                softWrap,
                overflow,
                density,
                layoutDirection,
                fontFamilyResolver,
                constraints
            ),
            multiParagraph,
            size
        )
    }

    companion object {
        /**
         * Paints the text onto the given canvas.
         *
         * Valid only after [layout] has been called.
         *
         * If you cannot see the text being painted, check that your text color does not conflict
         * with the background on which you are drawing. The default text color is white (to
         * contrast with the default black background color), so if you are writing an
         * application with a white background, the text will not be visible by default.
         *
         * To set the text style, specify a [SpanStyle] when creating the [AnnotatedString] that
         * you pass to the [TextDelegate] constructor or to the [text] property.
         */
        fun paint(canvas: Canvas, textLayoutResult: NewTextLayoutResult) {
            TextPainter.paint(canvas, textLayoutResult)
        }
    }
}