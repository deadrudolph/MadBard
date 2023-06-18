package com.deadrudolph.feature_player_domain.di.module

import com.deadrudolph.feature_player_domain.data.repository.PlayerRepositoryImpl
import com.deadrudolph.feature_player_domain.domain.repository.PlayerRepository
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoryModule {

    @Binds
    fun getPlayerRepository(
        playerRepositoryImpl: PlayerRepositoryImpl
    ): PlayerRepository
}