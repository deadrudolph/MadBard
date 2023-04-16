package com.deadrudolph.home_domain.domain.repository

import com.deadrudolph.home_domain.domain.model.songs_dashboard.SongItem
import com.puls.stateutil.Result

interface HomeRepository {

    suspend fun getAllSongs(): Result<List<SongItem>>

    suspend fun saveSongs(vararg songs : SongItem)
}