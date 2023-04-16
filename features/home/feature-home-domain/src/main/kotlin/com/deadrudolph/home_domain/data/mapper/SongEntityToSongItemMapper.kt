package com.deadrudolph.home_domain.data.mapper

import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.commondatabase.model.SongEntity
import com.deadrudolph.home_domain.domain.model.songs_dashboard.Chord
import com.deadrudolph.home_domain.domain.model.songs_dashboard.SongItem

class SongEntityToSongItemMapper {
    operator fun invoke(songEntity: SongEntity): SongItem {
        return with(songEntity) {
            SongItem(
                id = id,
                title = title,
                imagePath = imagePath,
                text = text,
                chords = chords.map { chordsEntity ->
                    Chord(
                        position = chordsEntity.position,
                        chordType = ChordType.valueOf(chordsEntity.chordType)
                    )
                }
            )
        }
    }
}