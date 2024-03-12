package com.deadrudolph.feature_player_domain.domain.usecase

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.common_domain.model.Result

interface GetSongByIdUseCase {

    suspend operator fun invoke(songId: String): Result<SongItem>
}
