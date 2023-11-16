package com.deadrudolph.home_domain.di.module

import com.deadrudolph.home_domain.data.repository.HomeRepositoryImpl
import com.deadrudolph.home_domain.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoryModule {

    @Binds
    fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}