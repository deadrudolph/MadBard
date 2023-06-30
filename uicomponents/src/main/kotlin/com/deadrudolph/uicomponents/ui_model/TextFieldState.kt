package com.deadrudolph.uicomponents.ui_model

import androidx.compose.ui.text.input.TextFieldValue
import com.deadrudolph.common_domain.model.ChordBlock

data class TextFieldState(
    val isFocused: Boolean = false,
    val value: TextFieldValue,
    val chordsList: List<ChordUIModel>,
    val chordBlock: ChordBlock? = null
)
