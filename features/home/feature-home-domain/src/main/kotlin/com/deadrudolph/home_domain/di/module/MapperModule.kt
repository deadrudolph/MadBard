package com.deadrudolph.home_domain.di.module

import com.deadrudolph.home_domain.data.mapper.SongEntityToSongItemMapper
import com.deadrudolph.home_domain.data.mapper.SongItemToSongEntityMapper
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
internal class MapperModule {

    @Provides
    @Reusable
    fun provideSongEntityToSongItemMapper() : SongEntityToSongItemMapper =
        SongEntityToSongItemMapper()

    @Provides
    @Reusable
    fun provideSongItemToSongEntityMapper() : SongItemToSongEntityMapper =
        SongItemToSongEntityMapper()

}