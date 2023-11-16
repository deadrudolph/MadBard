package com.deadrudolph.commondatabase.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.deadrudolph.common_domain.model.ChordGroup
import com.deadrudolph.commondatabase.constants.DBConstants

@Entity(
    tableName = DBConstants.CHORDS_TABLE_NAME
)
data class ChordTypeEntity(
    @PrimaryKey val marker: String,
    val scheme: List<Int>,
    val chordGroup: ChordGroup,
    val regexCondition: String = marker
)
