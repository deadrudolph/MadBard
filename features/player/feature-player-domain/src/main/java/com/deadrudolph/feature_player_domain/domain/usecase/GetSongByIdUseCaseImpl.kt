package com.deadrudolph.feature_player_domain.domain.usecase

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_player_domain.domain.repository.PlayerRepository
import com.puls.stateutil.Result
import javax.inject.Inject

internal class GetSongByIdUseCaseImpl @Inject constructor(
    private val playerRepository: PlayerRepository
): GetSongByIdUseCase {
    override suspend fun invoke(songId: String): Result<SongItem> {
        return playerRepository.getSongByIdFromDB(songId)
    }
}