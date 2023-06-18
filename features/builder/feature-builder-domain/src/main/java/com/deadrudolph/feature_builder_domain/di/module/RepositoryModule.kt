package com.deadrudolph.feature_builder_domain.di.module

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.deadrudolph.feature_builder_domain.data.repository.SongBuilderRepositoryImpl
import com.deadrudolph.feature_builder_domain.domain.repository_abs.SongBuilderRepository
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
internal class RepositoryModule {

    @Provides
    fun getSongBuilderRepository(
        songsDao: SongsDao,
        songItemToSongEntityMapper: SongItemToSongEntityMapper
    ): SongBuilderRepository = SongBuilderRepositoryImpl(
        songsDao,
        songItemToSongEntityMapper
    )
}