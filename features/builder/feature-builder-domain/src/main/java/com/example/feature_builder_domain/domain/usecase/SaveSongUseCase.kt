package com.example.feature_builder_domain.domain.usecase

import com.deadrudolph.common_domain.model.SongItem

interface SaveSongUseCase {

    suspend operator fun invoke(song: SongItem)
}