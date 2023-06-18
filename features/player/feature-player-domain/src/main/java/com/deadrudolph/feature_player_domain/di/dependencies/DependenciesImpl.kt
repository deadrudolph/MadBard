package com.deadrudolph.feature_player_domain.di.dependencies

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.di.component.DatabaseComponentHolder
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.feature_player_domain.di.component.PlayerDomainComponentInternal.Dependencies

internal class DependenciesImpl: Dependencies {
    override val songsDao: SongsDao
        get() = DatabaseComponentHolder.get().songsDao()

    override val songEntityToSongItemMapper: SongEntityToSongItemMapper
        get() = DatabaseComponentHolder.get().songEntityToSongItemMapper()

}