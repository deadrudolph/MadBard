package com.deadrudolph.home_domain.di.dependencies

import com.deadrudolph.commonnetwork.di.component.NetworkComponentHolder
import com.deadrudolph.home_domain.di.component.HomeDomainComponentInternal
import retrofit2.Retrofit

internal class DependenciesImpl : HomeDomainComponentInternal.Dependencies {

    override val retrofit: Retrofit
        get() = NetworkComponentHolder.get().networkClient()
}