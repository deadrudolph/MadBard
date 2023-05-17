package com.deadrudolph.feature_builder.presentation.ui.model

import androidx.compose.ui.text.input.TextFieldValue

internal data class TextFieldState(
    val isFocused: Boolean,
    val value: TextFieldValue,
    val chordsList: List<ChordUIModel>
)
