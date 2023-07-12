package com.deadrudolph.feature_builder.util.extension

import androidx.compose.ui.geometry.Offset
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
                    cursor?.left ?: 0f, cursor?.bottomCenter?.y ?: 0f
                )
            ) ?: 0
            val chordNotRelativePosition = (textLayoutResult?.getOffsetForPosition(
                Offset(
                    cursor?.left ?: 0f, cursor?.center?.y ?: 0f
                )
            ) ?: 0) + (allLines.getOrNull(currentLineIndex)?.length ?: 0)

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
                .filter { it.isChordsOnly() || it.isChordsBlock() }
                .sumOf { it.length }

            val position = if (chordHorizontalPosition > lastCharPosition ||
                allLines.indices.last == currentLineIndex
            ) chordNotRelativePosition else chordRelativePosition

            val offset = position - chordLinesLength

            listOfChords.add(
                Chord(
                    position = offset,
                    chordType = chord
                )
            )
        }
    }

    return listOfChords
}

fun String.getChordBlock(index: Int): ChordBlock {
    val chords = getChordTypesList()
    val title = removeAllChords().trimEnd()
    return ChordBlock(
        index = index,
        title = title,
        chordsList = chords
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


private fun String.getChordTypesList(): List<ChordType> {
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
