package com.deadrudolph.feature_builder.util.mapper

import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordBlock
import com.deadrudolph.feature_builder.ui_model.CalculatedSongModel
import com.deadrudolph.uicomponents.ui_model.TextFieldState

internal class TextFieldStateListToCalculatedSongMapper {

    operator fun invoke(stateList: List<TextFieldState>): CalculatedSongModel {
        val text = StringBuilder()
        val chords = arrayListOf<Chord>()
        val chordBlocks = arrayListOf<ChordBlock>()
        stateList.forEach { textValueState ->
            chords.addAll(
                textValueState.chordsList.map { uiChord ->
                    Chord(
                        position = uiChord.position + text.length,
                        chordType = uiChord.chordType,
                        positionOverlapCharCount = uiChord.positionOverlapCharCount
                    )
                }
            )
            textValueState.chordBlock?.let { chordBlock ->
                chordBlocks.add(
                    ChordBlock(
                        title = chordBlock.title,
                        chordsList = chordBlock.chordsList,
                        charIndex = text.length
                    )
                )
            }
            text.append(textValueState.value.text.addSingleNewLineChar())
        }
        return CalculatedSongModel(
            songText = text.toString(),
            chords = chords,
            chordBlocks = chordBlocks
        )
    }

    private fun String.addSingleNewLineChar(): String {
        return this.trimEnd() + "\n"
    }
}
