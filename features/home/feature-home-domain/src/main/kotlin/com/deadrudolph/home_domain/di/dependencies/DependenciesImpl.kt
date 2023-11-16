package com.deadrudolph.home_domain.di.dependencies

import com.deadrudolph.commondatabase.dao.ChordsDao
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.di.component.DatabaseComponentHolder
import com.deadrudolph.commondatabase.mapper.ChordEntityToChordMapper
import com.deadrudolph.commondatabase.mapper.ChordTypeToChordEntityMapper
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.deadrudolph.commonnetwork.di.component.NetworkComponentHolder
import com.deadrudolph.home_domain.di.component.HomeDomainComponentInternal
import retrofit2.Retrofit

internal class DependenciesImpl : HomeDomainComponentInternal.Dependencies {

    override val retrofit: Retrofit
        get() = NetworkComponentHolder.get().networkClient()

    override val songsDao: SongsDao
        get() = DatabaseComponentHolder.get().songsDao()

    override val chordsDao: ChordsDao
        get() = DatabaseComponentHolder.get().chordsDao()

    override val songEntityToSongItemMapper: SongEntityToSongItemMapper
        get() = DatabaseComponentHolder.get().songEntityToSongItemMapper()

    override val songItemToSongEntityMapper: SongItemToSongEntityMapper
        get() = DatabaseComponentHolder.get().songItemToSongEntityMapper()
    override val chordTypeToChordEntityMapper: ChordTypeToChordEntityMapper
        get() = DatabaseComponentHolder.get().chordTypeToChordEntityMapper()
    override val chordEntityToChordMapper: ChordEntityToChordMapper
        get() = DatabaseComponentHolder.get().chordEntityToChordTypeMapper()
}
