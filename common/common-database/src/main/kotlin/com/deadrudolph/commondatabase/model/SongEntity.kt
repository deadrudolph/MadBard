package com.deadrudolph.commondatabase.model

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.commondatabase.constants.DBConstants

@Entity(
    tableName = DBConstants.SONGS_TABLE_NAME
)
@Keep
data class SongEntity(
    @PrimaryKey val id: String,
    val imagePath: String,
    val title: String,
    val text: String,
    val chords: List<ChordEntity>,
    val chordBlocks: List<ChordBlockEntity>
)

@Keep
data class ChordEntity(
    val position: Int,
    val chordType: String
)

@Keep
data class ChordBlockEntity(
    val index: Int,
    val title: String,
    val chordsList: List<ChordType>
)