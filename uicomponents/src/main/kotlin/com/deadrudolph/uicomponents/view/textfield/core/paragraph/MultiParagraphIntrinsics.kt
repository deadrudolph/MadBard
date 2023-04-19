package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.Density
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import com.deadrudolph.uicomponents.view.textfield.core.string.mapEachParagraphStyle
import com.deadrudolph.uicomponents.view.textfield.core.style.TextStyle
import com.deadrudolph.uicomponents.view.textfield.extension.fastAny
import com.deadrudolph.uicomponents.view.textfield.extension.fastFilter
import com.deadrudolph.uicomponents.view.textfield.extension.fastMap
import com.deadrudolph.uicomponents.view.textfield.extension.fastMaxBy
import com.deadrudolph.uicomponents.view.textfield.extension.intersect


class MultiParagraphIntrinsics(
    val annotatedString: AnnotatedString,
    style: TextStyle,
    val placeholders: List<AnnotatedString.Range<Placeholder>>,
    density: Density,
    fontFamilyResolver: FontFamily.Resolver
) : NewParagraphIntrinsics {

    // NOTE(text-perf-review): why are we using lazy here? Are there cases where these
    // calculations aren't executed?
    override val minIntrinsicWidth: Float by lazy(LazyThreadSafetyMode.NONE) {
        infoList.fastMaxBy {
            it.intrinsics.minIntrinsicWidth
        }?.intrinsics?.minIntrinsicWidth ?: 0f
    }

    override val maxIntrinsicWidth: Float by lazy(LazyThreadSafetyMode.NONE) {
        infoList.fastMaxBy {
            it.intrinsics.maxIntrinsicWidth
        }?.intrinsics?.maxIntrinsicWidth ?: 0f
    }

    /**
     * [NewParagraphIntrinsics] for each paragraph included in the [buildAnnotatedString]. For empty string
     * there will be a single empty paragraph intrinsics info.
     */
    internal val infoList: List<ParagraphIntrinsicInfo>

    init {
        val paragraphStyle = style.toParagraphStyle()
        infoList = annotatedString
            .mapEachParagraphStyle(paragraphStyle) { annotatedString, paragraphStyleItem ->
                val currentParagraphStyle = resolveTextDirection(
                    paragraphStyleItem.item,
                    paragraphStyle
                )

                ParagraphIntrinsicInfo(
                    intrinsics = ParagraphIntrinsics(
                        text = annotatedString.text,
                        style = style.merge(currentParagraphStyle),
                        spanStyles = annotatedString.spanStyles,
                        placeholders = placeholders.getLocalPlaceholders(
                            paragraphStyleItem.start,
                            paragraphStyleItem.end
                        ),
                        density = density,
                        fontFamilyResolver = fontFamilyResolver
                    ),
                    startIndex = paragraphStyleItem.start,
                    endIndex = paragraphStyleItem.end
                )
            }
    }

    override val hasStaleResolvedFonts: Boolean
        get() = infoList.fastAny { it.intrinsics.hasStaleResolvedFonts }

    /**
     * if the [style] does `not` have [TextDirection] set, it will return a new
     * [ParagraphStyle] where [TextDirection] is set using the [defaultStyle]. Otherwise
     * returns the same [style] object.
     *
     * @param style ParagraphStyle to be checked for [TextDirection]
     * @param defaultStyle [ParagraphStyle] passed to [MultiParagraphIntrinsics] as the main style
     */
    private fun resolveTextDirection(
        style: ParagraphStyle,
        defaultStyle: ParagraphStyle
    ): ParagraphStyle {
        return style.textDirection?.let { style } ?: style.copy(
            textDirection = defaultStyle.textDirection
        )
    }
}

private fun List<AnnotatedString.Range<Placeholder>>.getLocalPlaceholders(start: Int, end: Int) =
    fastFilter { intersect(start, end, it.start, it.end) }.fastMap {
        require(start <= it.start && it.end <= end) {
            "placeholder can not overlap with paragraph."
        }
        AnnotatedString.Range(it.item, it.start - start, it.end - start)
    }

internal data class ParagraphIntrinsicInfo(
    val intrinsics: NewParagraphIntrinsics,
    val startIndex: Int,
    val endIndex: Int
)