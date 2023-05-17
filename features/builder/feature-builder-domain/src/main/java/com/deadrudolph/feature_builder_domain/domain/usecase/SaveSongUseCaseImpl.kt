package com.deadrudolph.feature_builder_domain.domain.usecase

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder_domain.domain.repository_abs.SongBuilderRepository
import javax.inject.Inject

internal class SaveSongUseCaseImpl @Inject constructor(
    private val songBuilderRepository: SongBuilderRepository
) : SaveSongUseCase {
    override suspend fun invoke(song: SongItem) {
        songBuilderRepository.saveSong(song)
    }
}