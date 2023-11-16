package com.deadrudolph.commondatabase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.deadrudolph.commondatabase.constants.DBConstants
import com.deadrudolph.commondatabase.dao.ChordsDao
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.model.ChordTypeEntity
import com.deadrudolph.commondatabase.model.SongEntity
import com.deadrudolph.commondatabase.type_converter.GeneralTypeConverter

@Database(
    entities = [
        SongEntity::class,
        ChordTypeEntity::class
    ],
    version = DBConstants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    GeneralTypeConverter::class
)
abstract class MadBardDatabase : RoomDatabase() {

    abstract fun getSongsDao(): SongsDao

    abstract fun getChordsDao(): ChordsDao
}
