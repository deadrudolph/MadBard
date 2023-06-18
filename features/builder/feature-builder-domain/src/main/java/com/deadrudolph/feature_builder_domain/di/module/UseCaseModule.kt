package com.deadrudolph.feature_builder_domain.di.module

import com.deadrudolph.feature_builder_domain.domain.repository_abs.SongBuilderRepository
import com.deadrudolph.feature_builder_domain.domain.usecase.SaveSongUseCase
import com.deadrudolph.feature_builder_domain.domain.usecase.SaveSongUseCaseImpl
import dagger.Module
import dagger.Provides

@Module
internal class UseCaseModule {

    @Provides
    fun getSaveSongUseCase(
        songBuilderRepository: SongBuilderRepository
    ): SaveSongUseCase = SaveSongUseCaseImpl(songBuilderRepository)
}