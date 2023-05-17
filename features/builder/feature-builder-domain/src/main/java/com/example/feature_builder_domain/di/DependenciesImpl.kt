package com.example.feature_builder_domain.di

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.di.component.DatabaseComponentHolder
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.example.feature_builder_domain.di.component.SongBuilderDomainComponentInternal.Dependencies

class DependenciesImpl : Dependencies {

    override val songsDao: SongsDao
        get() = DatabaseComponentHolder.get().songsDao()

    override val songItemToSongEntityMapper: SongItemToSongEntityMapper
        get() = DatabaseComponentHolder.get().songItemToSongEntityMapper()
}