package com.deadrudolph.feature_builder.di.dependencies

import com.deadrudolph.feature_builder.di.component.SongBuilderComponentInternal
import com.example.feature_builder_domain.di.component.SongBuilderDomainComponentHolder
import com.example.feature_builder_domain.domain.usecase.SaveSongUseCase

class DependenciesImpl : SongBuilderComponentInternal.Dependencies {
    override val saveSongUseCase: SaveSongUseCase
        get() = SongBuilderDomainComponentHolder.get().saveSongsUseCase()
}