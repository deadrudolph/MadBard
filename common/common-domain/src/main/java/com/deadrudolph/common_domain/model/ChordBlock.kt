package com.deadrudolph.common_domain.model

data class ChordBlock(
    val charIndex: Int,
    val title: String,
    val chordsList: List<ChordType>
)
