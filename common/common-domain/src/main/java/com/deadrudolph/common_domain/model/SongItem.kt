package com.deadrudolph.common_domain.model

data class SongItem(
    val id: String,
    val title: String,
    val imagePath: String,
    val text: String,
    val chords: List<Chord>
)
