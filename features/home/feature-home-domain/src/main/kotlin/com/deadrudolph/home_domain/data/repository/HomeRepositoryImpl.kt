package com.deadrudolph.home_domain.data.repository

import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.commondatabase.dao.ChordsDao
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.mapper.ChordEntityToChordMapper
import com.deadrudolph.commondatabase.mapper.ChordTypeToChordEntityMapper
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.deadrudolph.commonnetwork.util.safeApiCall
import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.deadrudolph.common_domain.model.Result
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val songsDao: SongsDao,
    private val chordsDao: ChordsDao,
    private val songItemToSongEntityMapper: SongItemToSongEntityMapper,
    private val songEntityToSongItemMapper: SongEntityToSongItemMapper,
    private val chordTypeToChordEntityMapper: ChordTypeToChordEntityMapper,
    private val chordEntityToChordMapper: ChordEntityToChordMapper
) : HomeRepository {

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

    override suspend fun saveChords(vararg chords: ChordType) {
        val chordTypes = chords.map(chordTypeToChordEntityMapper::invoke)
        chordsDao.insertChords(*chordTypes.toTypedArray())
    }

    override suspend fun getAllChords(): Result<List<ChordType>> {
        return safeApiCall {
            Result.Success(
                chordsDao.getAllChords().map(chordEntityToChordMapper::invoke)
            )
        }
    }
}
