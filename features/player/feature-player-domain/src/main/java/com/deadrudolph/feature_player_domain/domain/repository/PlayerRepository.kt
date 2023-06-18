package com.deadrudolph.feature_player_domain.domain.repository

import com.deadrudolph.common_domain.model.SongItem
import com.puls.stateutil.Result

internal interface PlayerRepository {

    suspend fun getSongByIdFromDB(songId: String): Result<SongItem>
}