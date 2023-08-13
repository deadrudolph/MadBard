package com.deadrudolph.feature_builder.util.extension

import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.TextFieldState

fun TextFieldState.removeChord(chord: ChordUIModel): TextFieldState {
    return copy(
        chordsList = chordsList.toMutableList().apply {
            remove(chord)
        }
    )
}
