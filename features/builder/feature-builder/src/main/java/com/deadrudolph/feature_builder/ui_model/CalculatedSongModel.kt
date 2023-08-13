package com.deadrudolph.feature_builder.ui_model

import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordBlock

data class CalculatedSongModel(
    val songText: String,
    val chords: List<Chord>,
    val chordBlocks: List<ChordBlock>
)