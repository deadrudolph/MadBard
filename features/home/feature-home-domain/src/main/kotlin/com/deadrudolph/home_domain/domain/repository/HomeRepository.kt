package com.deadrudolph.home_domain.domain.repository

import com.deadrudolph.common_domain.model.SongItem
import com.puls.stateutil.Result

interface HomeRepository {

    suspend fun getAllSongs(): Result<List<SongItem>>

    suspend fun saveSongs(vararg songs: SongItem)
}