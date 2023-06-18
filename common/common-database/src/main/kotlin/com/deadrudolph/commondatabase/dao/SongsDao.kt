package com.deadrudolph.commondatabase.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.deadrudolph.commondatabase.constants.DBConstants
import com.deadrudolph.commondatabase.model.SongEntity
import com.puls.stateutil.Result
import kotlin.jvm.Throws

@Dao
interface SongsDao {

    @Query("SELECT * FROM ${DBConstants.SONGS_TABLE_NAME}")
    suspend fun getAllSongs(): List<SongEntity>

    @Query("SELECT * FROM ${DBConstants.SONGS_TABLE_NAME} WHERE id = :songId")
    suspend fun getSongById(songId: String): SongEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    @Throws(Exception::class)
    suspend fun insertSongs(vararg songs: SongEntity)
}