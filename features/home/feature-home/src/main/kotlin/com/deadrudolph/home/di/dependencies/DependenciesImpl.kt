package com.deadrudolph.home.di.dependencies

import com.deadrudolph.home.di.component.HomeComponentInternal
import com.deadrudolph.home_domain.di.component.HomeDomainComponentHolder
import com.deadrudolph.home_domain.domain.usecase.chords.GetAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.chords.SaveAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.save_songs.SaveSongsUseCase

internal class DependenciesImpl : HomeComponentInternal.Dependencies {

    override val getAllSongsUseCase: GetAllSongsUseCase
        get() = HomeDomainComponentHolder.get().getAllSongsUseCase()

    override val saveSongsUseCase: SaveSongsUseCase
        get() = HomeDomainComponentHolder.get().saveSongsUseCase()

    override val saveAllChordsUseCase: SaveAllChordsUseCase
        get() = HomeDomainComponentHolder.get().saveAllChordsUseCase()
    override val getAllChordsUseCase: GetAllChordsUseCase
        get() = HomeDomainComponentHolder.get().getAllChordsUseCase()
}
