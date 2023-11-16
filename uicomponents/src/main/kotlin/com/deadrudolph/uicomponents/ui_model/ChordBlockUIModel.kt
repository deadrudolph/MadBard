package com.deadrudolph.uicomponents.ui_model

import com.deadrudolph.common_domain.model.ChordType

data class ChordBlockUIModel(
    val fieldIndex: Int,
    val title: String,
    val chordsList: List<ChordType>
)
