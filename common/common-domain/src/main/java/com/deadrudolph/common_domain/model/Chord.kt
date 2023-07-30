package com.deadrudolph.common_domain.model

data class Chord(
    val position: Int,
    /**If the corresponding string is shorter than chord actual position*/
    val additionalCharOffset: Int = 0,
    val chordType: ChordType
)
