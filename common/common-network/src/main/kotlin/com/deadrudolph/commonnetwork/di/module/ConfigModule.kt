package com.deadrudolph.commonnetwork.di.module

import com.deadrudolph.commonnetwork.config.NetworkConfig
import com.deadrudolph.commonnetwork.config.NetworkConfigImpl
import dagger.Binds
import dagger.Module

@Module
internal interface ConfigModule {

    @Binds
    fun bindNetworkConfig(impl: NetworkConfigImpl): NetworkConfig
}