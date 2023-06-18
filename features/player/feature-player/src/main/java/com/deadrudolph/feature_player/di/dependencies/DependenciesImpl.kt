package com.deadrudolph.feature_player.di.dependencies

import com.deadrudolph.feature_player.di.component.PlayerComponentInternal.Dependencies
import com.deadrudolph.feature_player_domain.di.component.PlayerDomainComponentHolder
import com.deadrudolph.feature_player_domain.domain.usecase.GetSongByIdUseCase

class DependenciesImpl: Dependencies {
    override val getSongByIdUseCase: GetSongByIdUseCase
        get() = PlayerDomainComponentHolder.get().getSongByIdUseCase()
}