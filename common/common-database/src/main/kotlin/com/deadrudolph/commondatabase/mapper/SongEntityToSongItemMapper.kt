package com.deadrudolph.commondatabase.mapper

import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordBlock
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.commondatabase.model.SongEntity

class SongEntityToSongItemMapper {
    operator fun invoke(songEntity: SongEntity): SongItem {
        return with(songEntity) {
            SongItem(
                id = id,
                createTimeMillis = createTimeMillis,
                title = title,
                imagePath = imagePath,
                text = text,
                chords = chords.map { chordsEntity ->
                    Chord(
                        position = chordsEntity.position,
                        chordType = chordsEntity.chordType,
                        positionOverlapCharCount = chordsEntity.positionOverlapCharCount
                    )
                },
                chordBlocks = chordBlocks.map { value ->
                    ChordBlock(
                        title = value.title,
                        chordsList = value.chordsList,
                        charIndex = value.position
                    )
                }
            )
        }
    }
}
