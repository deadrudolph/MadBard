package com.deadrudolph.uicomponents.ui_model

import androidx.compose.ui.text.input.TextFieldValue

data class TextFieldState(
    val isFocused: Boolean = false,
    val value: TextFieldValue,
    val chordsList: List<ChordUIModel>
)
