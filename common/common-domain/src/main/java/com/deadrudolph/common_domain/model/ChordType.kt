package com.deadrudolph.common_domain.model

data class ChordType(
    val marker: String,
    val scheme: List<Int>,
    val chordGroup: ChordGroup,
    val regexCondition: String = marker
)
