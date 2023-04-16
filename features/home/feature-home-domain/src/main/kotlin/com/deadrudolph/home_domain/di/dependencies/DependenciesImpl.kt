package com.deadrudolph.home_domain.di.dependencies

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.di.component.DatabaseComponentHolder
import com.deadrudolph.commonnetwork.di.component.NetworkComponentHolder
import com.deadrudolph.home_domain.di.component.HomeDomainComponentInternal
import retrofit2.Retrofit

internal class DependenciesImpl : HomeDomainComponentInternal.Dependencies {

    override val retrofit: Retrofit
        get() = NetworkComponentHolder.get().networkClient()

    override val songsDao: SongsDao
        get() = DatabaseComponentHolder.get().songsDao()
}