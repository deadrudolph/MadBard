package com.deadrudolph.uicomponents.utils.helper

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.TextFieldState
import java.util.SortedMap
import timber.log.Timber

object SongUICompositionHelper {

    fun songItemToTextFieldsStateList(
        songItem: SongItem,
        textLayoutResult: TextLayoutResult
    ): List<TextFieldState> {

        return addChords(
            songItem = songItem,
            textLayoutResult = textLayoutResult
        )
            .addTrailingBlocks(
                songItem = songItem
            )
            .ifEmpty {
                TextFieldState(
                    isFocused = true,
                    value = TextFieldValue(
                        songItem.text
                    ),
                    chordsList = emptyList()
                ).run(::listOf)
            }
    }

    private fun addChords(
        songItem: SongItem,
        textLayoutResult: TextLayoutResult
    ): MutableList<TextFieldState> {
        val chordUIList = songItem.chords.map { chord ->
            chord.toUIModel(textLayoutResult)
        }

        val groupedList = chordUIList.groupBy { model ->
            textLayoutResult.getLineForOffset(model.position)
        }.toSortedMap().addFirstStringIfNotExist()

        val keysList = groupedList.keys.toList()
        return keysList.mapIndexed { index, key ->
            val textStartIndex = textLayoutResult.getLineStart(key)
            val nextIndex = index.inc()
            val textEndIndex = if (nextIndex in keysList.indices) {
                val lastStringOfCurrentBlock = keysList[nextIndex].dec()
                textLayoutResult.getLineEnd(lastStringOfCurrentBlock).dec()
            } else songItem.text.indices.last

            /**
             * Set a position which is related to the "parent" textFieldValue
             * instead of the global position
             * */
            val chordsList = groupedList[key]?.map { chord ->
                chord.copy(
                    position = chord.position - textStartIndex
                )
            }
            val text = songItem.text.substring(textStartIndex..textEndIndex).trimEnd()
            TextFieldState(
                isFocused = index == keysList.indices.last,
                value = TextFieldValue(
                    text = text,
                    selection = TextRange(text.length)
                ),
                chordsList = chordsList.orEmpty(),
                chordBlock = songItem.chordBlocks.find { it.index == index }
            )
        }.toMutableList()
    }

    private fun Chord.toUIModel(
        layoutResult: TextLayoutResult
    ): ChordUIModel {
        return ChordUIModel(
            chordType = chordType,
            position = position,
            horizontalOffset = try {
                val textLength = layoutResult.layoutInput.text.length
                if (position <= textLength) {
                    layoutResult.getHorizontalPosition(
                        position,
                        true
                    ).toInt()
                } else {
                    layoutResult.getHorizontalPosition(
                        1, true
                    ).toInt() * (position - textLength)
                }
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
                0
            }
        )
    }
}

private fun MutableList<TextFieldState>.addTrailingBlocks(
    songItem: SongItem
): MutableList<TextFieldState> {
    addAll(
        songItem.chordBlocks.filter { it.index !in indices }.sortedBy {
            it.index
        }.map { block ->
            TextFieldState(
                value = TextFieldValue(),
                chordsList = emptyList(),
                chordBlock = block
            )
        }
    )
    return this
}

private fun SortedMap<Int, List<ChordUIModel>>.addFirstStringIfNotExist(

): SortedMap<Int, List<ChordUIModel>> {
    return if (containsKey(0)) this
    else apply {
        set(0, null)
    }
}
