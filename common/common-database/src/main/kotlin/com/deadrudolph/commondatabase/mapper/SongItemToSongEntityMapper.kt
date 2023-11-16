package com.deadrudolph.commondatabase.mapper

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.commondatabase.model.ChordBlockEntity
import com.deadrudolph.commondatabase.model.ChordEntity
import com.deadrudolph.commondatabase.model.SongEntity

class SongItemToSongEntityMapper {
    operator fun invoke(songEntity: SongItem): SongEntity {
        return with(songEntity) {
            SongEntity(
                id = id,
                createTimeMillis = createTimeMillis,
                title = title,
                imagePath = imagePath,
                text = text,
                chords = chords.map { chord ->
                    ChordEntity(
                        position = chord.position,
                        chordType = chord.chordType,
                        positionOverlapCharCount = chord.positionOverlapCharCount
                    )
                },
                chordBlocks = chordBlocks.map { value ->
                    ChordBlockEntity(
                        chordsList = value.chordsList,
                        title = value.title,
                        position = value.charIndex
                    )
                }
            )
        }
    }
}
