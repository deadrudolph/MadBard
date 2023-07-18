package com.deadrudolph.common_domain.model

data class ChordBlock(
    val index: Int,
    val position: Int,
    val title: String,
    val chordsList: List<ChordType>
)