package com.deadrudolph.feature_player_domain.domain.usecase

import com.deadrudolph.common_domain.model.SongItem
import com.puls.stateutil.Result

interface GetSongByIdUseCase {

    suspend operator fun invoke(songId: String): Result<SongItem>
}