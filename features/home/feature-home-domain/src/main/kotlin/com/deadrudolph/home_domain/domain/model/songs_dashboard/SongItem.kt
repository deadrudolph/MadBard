package com.deadrudolph.home_domain.domain.model.songs_dashboard

data class SongItem(
    val id: String,
    val title: String,
    val imagePath: String,
    val text: String,
    val chords: List<Chord>
)
