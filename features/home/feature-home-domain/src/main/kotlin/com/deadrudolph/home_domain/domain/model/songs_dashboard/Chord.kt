package com.deadrudolph.home_domain.domain.model.songs_dashboard

import com.deadrudolph.common_domain.model.ChordType

data class Chord(
    val position: Int,
    val chordType: ChordType
)