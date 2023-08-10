package com.deadrudolph.uicomponents.ui_model

import androidx.compose.runtime.Immutable
import androidx.compose.ui.text.input.TextFieldValue

@Immutable
data class TextFieldState(
    val isFocused: Boolean = false,
    val value: TextFieldValue,
    val chordsList: List<ChordUIModel>,
    val chordBlock: ChordBlockUIModel? = null
)
