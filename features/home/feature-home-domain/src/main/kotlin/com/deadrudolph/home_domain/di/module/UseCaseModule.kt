package com.deadrudolph.home_domain.di.module

import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.deadrudolph.home_domain.domain.usecase.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase
import dagger.Module
import dagger.Provides

@Module
internal class UseCaseModule {

    @Provides
    fun provideGetAllSongsUseCase(
        homeRepository: HomeRepository
    ): GetAllSongsUseCase = GetAllSongsUseCase(homeRepository)

    @Provides
    fun provideSaveSongsUseCase(
        homeRepository: HomeRepository
    ): SaveSongsUseCase = SaveSongsUseCase(homeRepository)

}