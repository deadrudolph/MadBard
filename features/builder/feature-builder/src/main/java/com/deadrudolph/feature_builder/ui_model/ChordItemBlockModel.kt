package com.deadrudolph.feature_builder.ui_model

import com.deadrudolph.common_domain.model.ChordType

internal data class ChordItemBlockModel(
    val blockIndex: Int,
    val chordIndex: Int,
    val chordType: ChordType
)