package com.deadrudolph.home_domain.di.module

import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCaseImpl
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

}