package com.deadrudolph.commondatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deadrudolph.commondatabase.constants.DBConstants
import com.deadrudolph.commondatabase.model.ChordTypeEntity
import kotlin.jvm.Throws

@Dao
interface ChordsDao {

    @Query("SELECT * FROM ${DBConstants.CHORDS_TABLE_NAME}")
    suspend fun getAllChords(): List<ChordTypeEntity>

    @Query("SELECT * FROM ${DBConstants.CHORDS_TABLE_NAME} WHERE marker = :chordTypeMarker")
    suspend fun getChordByMarker(chordTypeMarker: String): ChordTypeEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(Exception::class)
    suspend fun insertChords(vararg chords: ChordTypeEntity)
}
