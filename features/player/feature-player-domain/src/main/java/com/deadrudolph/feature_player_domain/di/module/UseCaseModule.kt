package com.deadrudolph.feature_player_domain.di.module

import com.deadrudolph.feature_player_domain.domain.usecase.GetSongByIdUseCase
import com.deadrudolph.feature_player_domain.domain.usecase.GetSongByIdUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
internal interface UseCaseModule {

    @Binds
    fun getSongByIdUseCase(
        getSongByIdUseCase: GetSongByIdUseCaseImpl
    ): GetSongByIdUseCase

}