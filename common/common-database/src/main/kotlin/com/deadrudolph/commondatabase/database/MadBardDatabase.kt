package com.deadrudolph.commondatabase.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.deadrudolph.commondatabase.constants.DBConstants
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.model.SongEntity
import com.deadrudolph.commondatabase.type_converter.GeneralTypeConverter

@Database(
    entities = [
        SongEntity::class
    ],
    version = DBConstants.DATABASE_VERSION,
    exportSchema = false
)
@TypeConverters(
    GeneralTypeConverter::class
)
abstract class MadBardDatabase : RoomDatabase() {

    abstract fun getSongsDao(): SongsDao
}