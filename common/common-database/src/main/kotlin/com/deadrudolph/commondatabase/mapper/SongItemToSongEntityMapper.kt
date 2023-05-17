package com.deadrudolph.commondatabase.mapper

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.commondatabase.model.ChordEntity
import com.deadrudolph.commondatabase.model.SongEntity

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