package com.deadrudolph.commondatabase.di.component

import android.content.Context
import com.deadrudolph.commondatabase.dao.ChordsDao
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.di.dependencies.DependenciesImpl
import com.deadrudolph.commondatabase.di.module.DaoModule
import com.deadrudolph.commondatabase.di.module.DatabaseModule
import com.deadrudolph.commondatabase.di.module.MapperModule
import com.deadrudolph.commondatabase.mapper.ChordEntityToChordMapper
import com.deadrudolph.commondatabase.mapper.ChordTypeToChordEntityMapper
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.ComponentHolder
import dagger.Component
import javax.inject.Singleton

interface DatabaseComponent : DIComponent {

    fun songsDao(): SongsDao

    fun chordsDao(): ChordsDao

    fun songEntityToSongItemMapper(): SongEntityToSongItemMapper

    fun songItemToSongEntityMapper(): SongItemToSongEntityMapper

    fun chordEntityToChordTypeMapper(): ChordEntityToChordMapper

    fun chordTypeToChordEntityMapper(): ChordTypeToChordEntityMapper
}

@Singleton
@Component(
    modules = [
        DatabaseModule::class,
        DaoModule::class,
        MapperModule::class
    ],
    dependencies = [DatabaseComponentInternal.Dependencies::class]
)
internal interface DatabaseComponentInternal : DatabaseComponent {

    interface Dependencies {
        val context: Context
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): DatabaseComponentInternal
    }
}

object DatabaseComponentHolder : ComponentHolder<DatabaseComponent>() {

    override fun build(): DatabaseComponent {

        return DaggerDatabaseComponentInternal.factory().create(
            DependenciesImpl()
        )
    }

    internal fun getInternal(): DatabaseComponentInternal = get() as DatabaseComponentInternal
}
