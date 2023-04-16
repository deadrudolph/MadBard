package com.deadrudolph.uicomponents.view.textfield.extension

import androidx.compose.ui.text.TextRange
import androidx.emoji2.text.EmojiCompat
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