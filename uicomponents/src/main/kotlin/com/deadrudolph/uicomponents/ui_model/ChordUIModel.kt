package com.deadrudolph.uicomponents.ui_model

import com.deadrudolph.common_domain.model.ChordType

data class ChordUIModel(
    val chordType: ChordType,
    val horizontalOffset: Int,
    val position: Int
)
