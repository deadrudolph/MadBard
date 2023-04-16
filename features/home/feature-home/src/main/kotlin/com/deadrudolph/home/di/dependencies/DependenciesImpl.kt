package com.deadrudolph.home.di.dependencies

import com.deadrudolph.home.di.component.HomeComponentInternal
import com.deadrudolph.home_domain.di.component.HomeDomainComponentHolder
import com.deadrudolph.home_domain.domain.usecase.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase

internal class DependenciesImpl : HomeComponentInternal.Dependencies {

    override val getAllSongsUseCase: GetAllSongsUseCase
        get() = HomeDomainComponentHolder.get().getAllSongsUseCase()

    override val saveSongsUseCase: SaveSongsUseCase
        get() = HomeDomainComponentHolder.get().saveSongsUseCase()

}