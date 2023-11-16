package com.deadrudolph.commondatabase.di.module

import com.deadrudolph.commondatabase.dao.ChordsDao
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.database.MadBardDatabase
import dagger.Module
import dagger.Provides

@Module
class DaoModule {

    @Provides
    fun provideSongsDao(db: MadBardDatabase): SongsDao {
        return db.getSongsDao()
    }

    @Provides
    fun provideChordsDao(db: MadBardDatabase): ChordsDao {
        return db.getChordsDao()
    }
}
