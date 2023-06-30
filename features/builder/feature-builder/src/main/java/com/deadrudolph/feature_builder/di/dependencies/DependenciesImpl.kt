package com.deadrudolph.feature_builder.di.dependencies

import com.deadrudolph.feature_builder.di.component.SongBuilderComponentInternal
import com.deadrudolph.feature_builder_domain.di.component.SongBuilderDomainComponentHolder
import com.deadrudolph.feature_builder_domain.domain.usecase.SaveSongUseCase
import com.deadrudolph.home_domain.di.component.HomeDomainComponentHolder
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase

class DependenciesImpl : SongBuilderComponentInternal.Dependencies {
    override val saveSongUseCase: SaveSongUseCase
        get() = SongBuilderDomainComponentHolder.get().saveSongsUseCase()

    override val getAllSongsUseCase: GetAllSongsUseCase
        get() = HomeDomainComponentHolder.get().getAllSongsUseCase()
}