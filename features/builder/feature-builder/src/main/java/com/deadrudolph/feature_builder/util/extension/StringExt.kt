package com.deadrudolph.feature_builder.util.extension

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.text.TextLayoutResult
import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordBlock
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.feature_builder.ui_model.RawStringType
import com.deadrudolph.feature_builder.ui_model.RawStringType.CHORD
import com.deadrudolph.feature_builder.ui_model.RawStringType.CHORD_BLOCK
import com.deadrudolph.feature_builder.ui_model.RawStringType.TEXT
import com.deadrudolph.feature_builder.util.regex.CommonLanguagesRegex.emptySongLineRegex
import com.deadrudolph.feature_builder.util.regex.CommonLanguagesRegex.languagesRegexList
import com.deadrudolph.feature_builder.util.regex.CommonLanguagesRegex.noLetterRegexEnd
import com.deadrudolph.feature_builder.util.regex.CommonLanguagesRegex.noLetterRegexStart
import kotlin.math.roundToInt
import kotlin.text.RegexOption.IGNORE_CASE

internal fun String.getRawStringType(): RawStringType {
    return when {
        isChordsOnly() -> CHORD
        isChordsBlock() -> CHORD_BLOCK
        else -> TEXT
    }
}

fun String.getChordsList(
    textLayoutResult: TextLayoutResult?,
    prevTextSize: Int,
    allLines: List<String>,
    currentLineIndex: Int
): List<Chord> {

    val listOfChords = arrayListOf<Chord>()
    ChordType.values().forEach { chord ->
        val regex = """$noLetterRegexStart${chord.regexCondition}$noLetterRegexEnd"""
            .toRegex(IGNORE_CASE)
        regex.findAll(this).forEach { result ->
            val chordIndex = result.range.first.coerceAtMost(result.range.last)
            val cursor = textLayoutResult?.getCursorRect(
                chordIndex.coerceAtLeast(0) +
                        prevTextSize
            )
            val chordRelativePosition = textLayoutResult?.getOffsetForPosition(
                Offset(
                    cursor?.left ?: 0f, cursor?.bottomLeft?.y ?: 0f
                )
            ) ?: 0

            val chordHorizontalPosition = textLayoutResult?.getHorizontalPosition(
                prevTextSize + chordIndex,
                true
            ) ?: 0f
            val lastIndex = allLines.take(
                currentLineIndex + 2
            ).reduce { prev, next -> prev + next }.length

            val lastCharPosition = textLayoutResult?.getHorizontalPosition(
                lastIndex,
                true
            ) ?: 0f

            val chordLinesLength = allLines
                .take(currentLineIndex.inc())
                .filter { it.isChordsOnly() || it.isChordsBlock() }
                .sumOf { it.length }

            var additionalOffset = 0

            if (chordHorizontalPosition > lastCharPosition ||
                allLines.indices.last == currentLineIndex
            ) {
                additionalOffset = calculateAdditionalOffset(
                    textLayoutResult,
                    allLines,
                    cursor,
                )
            }

            val offset = chordRelativePosition - chordLinesLength

            listOfChords.add(
                Chord(
                    position = offset + additionalOffset,
                    chordType = chord,
                    positionOverlapCharCount = additionalOffset
                )
            )
        }
    }

    return listOfChords
}

private fun calculateAdditionalOffset(
    textLayoutResult: TextLayoutResult?,
    allLines: List<String>,
    cursor: Rect?
): Int {
    val lastLineWithFirstChar = allLines.takeWhile {
        it.first().toString().isNotBlank()
    }
    val indexOfFirstChar = when (lastLineWithFirstChar.size) {
        0 -> 0
        1 -> 1
        else -> allLines.take(allLines.size.dec()).sumOf { it.length }.inc()
    }
    val charCursor = textLayoutResult?.getCursorRect(indexOfFirstChar)?.center?.x ?: 1f
    val chordCursor = cursor?.center?.x ?: 0f
    return (chordCursor / charCursor).roundToInt()
}

fun String.getChordBlock(position: Int): ChordBlock {
    val chords = getChordTypesList()
    val title = removeAllChords().trimEnd()
    return ChordBlock(
        title = title,
        chordsList = chords,
        charIndex = position
    )
}

private fun String.isChordsOnly(): Boolean {
    if (isSongLineBlank(this)) return false
    val stringWithNoChords = removeAllChords()
    return isSongLineBlank(stringWithNoChords)
}

private fun isSongLineBlank(line: String): Boolean {
    return """^[^$emptySongLineRegex]*$""".toRegex().matches(line)
}


fun String.getChordTypesList(): List<ChordType> {
    val listOfChords = arrayListOf<Pair<Int, ChordType>>()
    ChordType.values().forEach { chord ->
        val regex = """$noLetterRegexStart${chord.regexCondition}$noLetterRegexEnd""".toRegex(
            IGNORE_CASE
        )
        regex.findAll(this).forEach { result ->
            listOfChords.add(result.range.first to chord)
        }
    }

    return listOfChords.sortedBy { it.first }.map { it.second }
}

private fun String.isChordsBlock(): Boolean {
    val stringWithNoChords = removeAllChords()
    return stringWithNoChords != this &&
            stringWithNoChords.contains("""$languagesRegexList""".toRegex())
}

private fun String.removeAllChords(): String {
    val listOfChords = ChordType.values().map { it.regexCondition }
    return replace(
        listOfChords.toRegexConditionsString().toRegex(IGNORE_CASE),
        ""
    )
}
