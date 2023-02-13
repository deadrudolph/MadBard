package com.deadrudolph.home_domain.di.module

import com.deadrudolph.home_domain.data.api.HomeApi
import dagger.Module
import dagger.Provides
import dagger.Reusable
import retrofit2.Retrofit

@Module
internal class NetworkModule {

    @Reusable
    @Provides
    fun provideHomeApi(retrofit: Retrofit): HomeApi {
        return retrofit.create(HomeApi::class.java)
    }
}