package com.deadrudolph.home_domain.di.module

import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.deadrudolph.home_domain.domain.usecase.chords.GetAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.chords.SaveAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCaseImpl
import com.deadrudolph.home_domain.domain.usecase.save_songs.SaveSongsUseCase
import dagger.Module
import dagger.Provides

@Module
internal class UseCaseModule {

    @Provides
    fun provideGetAllSongsUseCase(
        homeRepository: HomeRepository
    ): GetAllSongsUseCase = GetAllSongsUseCaseImpl(homeRepository)

    @Provides
    fun provideSaveSongsUseCase(
        homeRepository: HomeRepository
    ): SaveSongsUseCase = SaveSongsUseCase(homeRepository)

    @Provides
    fun provideGetChordsUseCase(
        homeRepository: HomeRepository
    ): GetAllChordsUseCase = GetAllChordsUseCase(homeRepository)

    @Provides
    fun provideSaveChordsUseCase(
        homeRepository: HomeRepository
    ): SaveAllChordsUseCase = SaveAllChordsUseCase(homeRepository)
}
