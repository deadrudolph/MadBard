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
import com.deadrudolph.uicomponents.utils.helper.getOneCharWidth
import kotlin.math.roundToInt
import kotlin.text.RegexOption.IGNORE_CASE

internal fun String.getRawStringType(allChords: List<ChordType>): RawStringType {
    return when {
        isChordsOnly(allChords) -> CHORD
        isChordsBlock(allChords) -> CHORD_BLOCK
        else -> TEXT
    }
}

fun String.getChordsList(
    allChords: List<ChordType>,
    textLayoutResult: TextLayoutResult?,
    prevTextSize: Int,
    allLines: List<String>,
    currentLineIndex: Int
): List<Chord> {

    val listOfChords = arrayListOf<Chord>()
    allChords.forEach { chord ->
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
            ).reduce { prev, next -> prev + next }.indices.last

            val lastCharPosition = textLayoutResult?.getHorizontalPosition(
                lastIndex,
                true
            ) ?: 0f

            val chordLinesLength = allLines
                .take(currentLineIndex.inc())
                .filter { it.isChordsOnly(allChords) || it.isChordsBlock(allChords) }
                .sumOf { it.length }

            var additionalOffset = 0

            if (chordHorizontalPosition > lastCharPosition) {
                additionalOffset = calculateAdditionalOffset(
                    textLayoutResult,
                    allLines,
                    chordHorizontalPosition - lastCharPosition,
                )
            }
            val defaultOffset = chordRelativePosition - chordLinesLength
            val offset = if (allLines.indices.last == currentLineIndex) {
                calculateLastStringOffset(
                    textLayoutResult = textLayoutResult,
                    allLines = allLines,
                    chordLinesLength = chordLinesLength,
                    cursor = cursor
                ) ?: defaultOffset
            } else defaultOffset
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
    overlapPx: Float
): Int {
    if (allLines.isEmpty() || (allLines.size == 1 && allLines.first().isEmpty())) return 0
    val lastLineWithFirstChar = arrayListOf<String>()
    run search@{
        allLines.forEach {
            lastLineWithFirstChar.add(it)
            if (it.first().toString().isNotBlank()) return@search
        }
    }

    val indexOfFirstChar = when (lastLineWithFirstChar.size) {
        1, 0 -> 1
        else -> allLines.take(allLines.size.dec()).sumOf { it.length }.inc()
    }
    return textLayoutResult?.getCursorRect(indexOfFirstChar)?.left?.let {
        if (it == 0f) return@let 0
        (overlapPx / it).roundToInt()
    } ?: 0
}

private fun calculateLastStringOffset(
    textLayoutResult: TextLayoutResult?,
    allLines: List<String>,
    chordLinesLength: Int,
    cursor: Rect?
): Int? {
    return textLayoutResult?.getOneCharWidth()?.let { oneChar ->
        val lastPosition = allLines.sumOf { it.length } - chordLinesLength
        val offset = (lastPosition + ((cursor?.left ?: 0f) / oneChar)).roundToInt()
        if (offset < 0) null
        else offset
    }
}

fun String.getChordBlock(
    allChords: List<ChordType>,
    position: Int
): ChordBlock {
    val chords = getChordTypesList(allChords)
    val title = removeAllChords(allChords).trimEnd()
    return ChordBlock(
        title = title,
        chordsList = chords,
        charIndex = position
    )
}

private fun String.isChordsOnly(allChords: List<ChordType>): Boolean {
    if (isSongLineBlank(this)) return false
    val stringWithNoChords = removeAllChords(allChords)
    return isSongLineBlank(stringWithNoChords)
}

private fun isSongLineBlank(line: String): Boolean {
    return """^[^$emptySongLineRegex]*$""".toRegex().matches(line)
}

fun String.getChordTypesList(allChords: List<ChordType>): List<ChordType> {
    val listOfChords = arrayListOf<Pair<Int, ChordType>>()
    allChords.forEach { chord ->
        val regex = """$noLetterRegexStart${chord.regexCondition}$noLetterRegexEnd""".toRegex(
            IGNORE_CASE
        )
        regex.findAll(this).forEach { result ->
            listOfChords.add(result.range.first to chord)
        }
    }

    return listOfChords.sortedBy { it.first }.map { it.second }
}

private fun String.isChordsBlock(allChords: List<ChordType>): Boolean {
    val stringWithNoChords = removeAllChords(allChords)
    return stringWithNoChords != this &&
        stringWithNoChords.contains("""$languagesRegexList""".toRegex())
}

private fun String.removeAllChords(allChords: List<ChordType>): String {
    val listOfChords = allChords.map { it.regexCondition }
    return replace(
        listOfChords.toRegexConditionsString().toRegex(IGNORE_CASE),
        ""
    )
}
