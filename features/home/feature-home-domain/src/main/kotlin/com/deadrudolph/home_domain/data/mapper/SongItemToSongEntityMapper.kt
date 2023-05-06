package com.deadrudolph.home_domain.data.mapper

import com.deadrudolph.commondatabase.model.ChordEntity
import com.deadrudolph.commondatabase.model.SongEntity
import com.deadrudolph.home_domain.domain.model.songs_dashboard.SongItem

class SongItemToSongEntityMapper {
    operator fun invoke(songEntity: SongItem): SongEntity {
        return with(songEntity) {
            SongEntity(
                id = id,
                title = title,
                imagePath = imagePath,
                text = text,
                chords = chords.map { chord ->
                    ChordEntity(
                        position = chord.position,
                        chordType = chord.chordType.name
                    )
                }
            )
        }
    }
}