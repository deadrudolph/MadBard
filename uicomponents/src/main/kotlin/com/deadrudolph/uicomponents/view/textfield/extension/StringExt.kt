package com.deadrudolph.uicomponents.view.textfield.extension

import androidx.emoji2.text.EmojiCompat
import com.deadrudolph.uicomponents.view.textfield.core.paragraph.ParagraphStyle
import com.deadrudolph.uicomponents.view.textfield.core.range.TextRange
import com.deadrudolph.uicomponents.view.textfield.core.span.SpanStyle
import com.deadrudolph.uicomponents.view.textfield.core.string.AnnotatedString
import java.text.BreakIterator

internal fun CharSequence.getParagraphBoundary(index: Int): TextRange {
    return TextRange(findParagraphStart(index), findParagraphEnd(index))
}

internal fun CharSequence.findParagraphStart(startIndex: Int): Int {
    for (index in startIndex - 1 downTo 1) {
        if (this[index - 1] == '\n') {
            return index
        }
    }
    return 0
}

internal fun CharSequence.findParagraphEnd(startIndex: Int): Int {
    for (index in startIndex + 1 until this.length) {
        if (this[index] == '\n') {
            return index
        }
    }
    return this.length
}

internal fun String.findFollowingBreak(index: Int): Int {
    val emojiBreak = getEmojiCompatIfLoaded()?.getEmojiEnd(this, index)?.takeUnless { it == -1 }
    if (emojiBreak != null) return emojiBreak

    val it = BreakIterator.getCharacterInstance()
    it.setText(this)
    return it.following(index)
}

internal fun getEmojiCompatIfLoaded(): EmojiCompat? =
    if (EmojiCompat.isConfigured())
        EmojiCompat.get().takeIf { it.loadState == EmojiCompat.LOAD_STATE_SUCCEEDED }
    else null

internal fun String.findPrecedingBreak(index: Int): Int {
    val emojiBreak = getEmojiCompatIfLoaded()
        ?.getEmojiStart(this, maxOf(0, index - 1))?.takeUnless { it == -1 }
    if (emojiBreak != null) return emojiBreak

    val it = BreakIterator.getCharacterInstance()
    it.setText(this)
    return it.preceding(index)
}

internal fun StringBuilder.appendCodePointX(codePoint: Int): StringBuilder =
    this.appendCodePoint(codePoint)

internal fun contains(baseStart: Int, baseEnd: Int, targetStart: Int, targetEnd: Int) =
    (baseStart <= targetStart && targetEnd <= baseEnd) &&
            (baseEnd != targetEnd || (targetStart == targetEnd) == (baseStart == baseEnd))
internal fun intersect(lStart: Int, lEnd: Int, rStart: Int, rEnd: Int) =
    maxOf(lStart, rStart) < minOf(lEnd, rEnd) ||
            contains(lStart, lEnd, rStart, rEnd) || contains(rStart, rEnd, lStart, lEnd)

internal inline fun <T> AnnotatedString.mapEachParagraphStyle(
    defaultParagraphStyle: ParagraphStyle,
    crossinline block: (
        annotatedString: AnnotatedString,
        paragraphStyle: AnnotatedString.Range<ParagraphStyle>
    ) -> T
): List<T> {
    return normalizedParagraphStyles(defaultParagraphStyle).fastMap { paragraphStyleRange ->
        val annotatedString = substringWithoutParagraphStyles(
            paragraphStyleRange.start,
            paragraphStyleRange.end
        )
        block(annotatedString, paragraphStyleRange)
    }
}

internal fun AnnotatedString.normalizedParagraphStyles(
    defaultParagraphStyle: ParagraphStyle
): List<AnnotatedString.Range<ParagraphStyle>> {
    val length = text.length
    val paragraphStyles = paragraphStyles

    var lastOffset = 0
    val result = mutableListOf<AnnotatedString.Range<ParagraphStyle>>()
    paragraphStyles.fastForEach { (style, start, end) ->
        if (start != lastOffset) {
            result.add(AnnotatedString.Range(defaultParagraphStyle, lastOffset, start))
        }
        result.add(AnnotatedString.Range(defaultParagraphStyle.merge(style), start, end))
        lastOffset = end
    }
    if (lastOffset != length) {
        result.add(AnnotatedString.Range(defaultParagraphStyle, lastOffset, length))
    }
    // This is a corner case where annotatedString is an empty string without any ParagraphStyle.
    // In this case, an empty ParagraphStyle is created.
    if (result.isEmpty()) {
        result.add(AnnotatedString.Range(defaultParagraphStyle, 0, 0))
    }
    return result
}

internal fun AnnotatedString.substringWithoutParagraphStyles(
    start: Int,
    end: Int
): AnnotatedString {
    return AnnotatedString(
        text = if (start != end) text.substring(start, end) else "",
        spanStyles = getLocalSpanStyles(start, end) ?: emptyList(),
    )
}

private fun AnnotatedString.getLocalSpanStyles(
    start: Int,
    end: Int
): List<AnnotatedString.Range<SpanStyle>>? {
    if (start == end) return null
    val spanStyles = spanStyles
    // If the given range covers the whole AnnotatedString, return SpanStyles without conversion.
    if (start == 0 && end >= this.text.length) {
        return spanStyles
    }
    return spanStyles.fastFilter {
        intersect(
            start,
            end,
            it.start,
            it.end
        )
    }
        .fastMap {
            AnnotatedString.Range(
                it.item,
                it.start.coerceIn(start, end) - start,
                it.end.coerceIn(start, end) - start
            )
        }
}