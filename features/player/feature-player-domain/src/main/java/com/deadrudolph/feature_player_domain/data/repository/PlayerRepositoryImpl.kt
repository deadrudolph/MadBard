package com.deadrudolph.feature_player_domain.data.repository

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.commonnetwork.util.safeApiCall
import com.deadrudolph.feature_player_domain.domain.repository.PlayerRepository
import com.deadrudolph.common_domain.model.Result
import javax.inject.Inject

internal class PlayerRepositoryImpl @Inject constructor(
    private val songsDao: SongsDao,
    private val songEntityToSongItemMapper: SongEntityToSongItemMapper
) : PlayerRepository {

    override suspend fun getSongByIdFromDB(songId: String): Result<SongItem> {
        return safeApiCall {
            Result.Success(
                songsDao.getSongById(songId).run(songEntityToSongItemMapper::invoke)
            )
        }
    }
}
