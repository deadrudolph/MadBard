package com.deadrudolph.home.di.dependencies

import com.deadrudolph.home.di.component.HomeComponentInternal
import com.deadrudolph.home_domain.di.component.HomeDomainComponentHolder
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase

internal class DependenciesImpl : HomeComponentInternal.Dependencies {

    override val getAllSongsUseCase: GetAllSongsUseCase
        get() = HomeDomainComponentHolder.get().getAllSongsUseCase()

    override val saveSongsUseCase: SaveSongsUseCase
        get() = HomeDomainComponentHolder.get().saveSongsUseCase()

}