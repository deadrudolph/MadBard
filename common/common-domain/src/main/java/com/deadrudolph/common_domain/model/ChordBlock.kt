package com.deadrudolph.common_domain.model

data class ChordBlock(
    val index: Int,
    val title: String,
    val chordsList: List<ChordType>
)