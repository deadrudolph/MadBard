package com.deadrudolph.feature_builder.presentation.ui.model

import com.deadrudolph.common_domain.model.ChordType

internal data class ChordUIModel(
    val chordType: ChordType,
    val horizontalOffset: Int,
    val position: Int
)
