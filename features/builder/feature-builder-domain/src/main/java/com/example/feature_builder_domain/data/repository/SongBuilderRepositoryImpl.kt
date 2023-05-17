package com.example.feature_builder_domain.data.repository

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.example.feature_builder_domain.domain.repository_abs.SongBuilderRepository
import javax.inject.Inject

internal class SongBuilderRepositoryImpl @Inject constructor(
    private val songsDao: SongsDao,
    private val songItemToSongEntityMapper: SongItemToSongEntityMapper
) : SongBuilderRepository {

    override suspend fun saveSong(songItem: SongItem) {
        songsDao.insertSongs(
            songItemToSongEntityMapper(songItem)
        )
    }
}