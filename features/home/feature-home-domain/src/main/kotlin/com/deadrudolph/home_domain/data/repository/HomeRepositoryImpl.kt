package com.deadrudolph.home_domain.data.repository

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commonnetwork.util.safeApiCall
import com.deadrudolph.home_domain.data.mapper.SongEntityToSongItemMapper
import com.deadrudolph.home_domain.data.mapper.SongItemToSongEntityMapper
import com.deadrudolph.home_domain.domain.model.songs_dashboard.SongItem
import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.puls.stateutil.Result
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val songsDao: SongsDao,
    private val songItemToSongEntityMapper: SongItemToSongEntityMapper,
    private val songEntityToSongItemMapper: SongEntityToSongItemMapper
): HomeRepository {

    override suspend fun getAllSongs(): Result<List<SongItem>> {
        return safeApiCall {
            Result.Success(
                songsDao.getAllSongs().map(songEntityToSongItemMapper::invoke)
            )
        }
    }

    override suspend fun saveSongs(vararg songs: SongItem) {
        val songsList = songs.map(songItemToSongEntityMapper::invoke)
        songsDao.insertSongs(*songsList.toTypedArray())
    }
}