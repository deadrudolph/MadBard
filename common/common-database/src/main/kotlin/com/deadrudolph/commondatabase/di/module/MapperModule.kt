package com.deadrudolph.commondatabase.di.module

import com.deadrudolph.commondatabase.mapper.ChordEntityToChordMapper
import com.deadrudolph.commondatabase.mapper.ChordTypeToChordEntityMapper
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
internal class MapperModule {

    @Provides
    @Reusable
    fun provideSongEntityToSongItemMapper(): SongEntityToSongItemMapper =
        SongEntityToSongItemMapper()

    @Provides
    @Reusable
    fun provideSongItemToSongEntityMapper(): SongItemToSongEntityMapper =
        SongItemToSongEntityMapper()

    @Provides
    @Reusable
    fun provideChordEntityToChordMapper(): ChordEntityToChordMapper =
        ChordEntityToChordMapper()

    @Provides
    @Reusable
    fun provideChordToChordTypeMapper(): ChordTypeToChordEntityMapper =
        ChordTypeToChordEntityMapper()
}
