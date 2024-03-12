package com.deadrudolph.home_domain.domain.repository

import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.common_domain.model.Result

interface HomeRepository {

    suspend fun getAllSongs(): Result<List<SongItem>>

    suspend fun saveSongs(vararg songs: SongItem)

    suspend fun saveChords(vararg chords: ChordType)

    suspend fun getAllChords(): Result<List<ChordType>>
}
