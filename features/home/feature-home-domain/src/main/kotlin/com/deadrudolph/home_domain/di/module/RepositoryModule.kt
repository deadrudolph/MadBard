package com.deadrudolph.home_domain.di.module

import com.deadrudolph.home_domain.data.repository.users.UsersRepository
import com.deadrudolph.home_domain.data.repository.users.UsersRepositoryImpl
import dagger.Binds
import dagger.Module

@Module
internal interface RepositoryModule {

    @Binds
    fun bindHomeRepository(impl: UsersRepositoryImpl): UsersRepository
}