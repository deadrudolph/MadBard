package com.deadrudolph.feature_builder.util.mapper

import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.uicomponents.ui_model.TextFieldState

internal class TextFieldStateListToTextAndChordsMapper {

    operator fun invoke(stateList: List<TextFieldState>): Pair<String, List<Chord>> {
        val text = StringBuilder()
        val chords = arrayListOf<Chord>()
        stateList.forEach { textValueState ->
            chords.addAll(
                textValueState.chordsList.map { uiChord ->
                    Chord(
                        position = uiChord.position + text.length,
                        chordType = uiChord.chordType
                    )
                }
            )
            text.append(textValueState.value.text.addSingleNewLineChar())
        }
        return text.toString() to chords
    }

    private fun String.addSingleNewLineChar(): String {
        return this.trimEnd() + "\n"
    }
}