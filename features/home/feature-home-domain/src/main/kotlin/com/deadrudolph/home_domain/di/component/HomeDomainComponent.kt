package com.deadrudolph.home_domain.di.component

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.home_domain.di.dependencies.DependenciesImpl
import com.deadrudolph.home_domain.di.module.MapperModule
import com.deadrudolph.home_domain.di.module.NetworkModule
import com.deadrudolph.home_domain.di.module.RepositoryModule
import com.deadrudolph.home_domain.di.module.UseCaseModule
import com.deadrudolph.home_domain.domain.usecase.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase
import dagger.Component
import retrofit2.Retrofit

interface HomeDomainComponent : DIComponent {

    fun saveSongsUseCase(): SaveSongsUseCase

    fun getAllSongsUseCase(): GetAllSongsUseCase
}

@Component(
    modules = [
        NetworkModule::class,
        RepositoryModule::class,
        UseCaseModule::class,
        MapperModule::class
    ],

    dependencies = [
        HomeDomainComponentInternal.Dependencies::class,
    ]
)
internal interface HomeDomainComponentInternal : HomeDomainComponent {

    interface Dependencies {
        val retrofit: Retrofit
        val songsDao: SongsDao
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): HomeDomainComponentInternal
    }
}

object HomeDomainComponentHolder : FeatureComponentHolder<HomeDomainComponent>() {

    override fun build(): HomeDomainComponent {

        return DaggerHomeDomainComponentInternal.factory().create(
            DependenciesImpl()
        )
    }

    internal fun getInternal(): HomeDomainComponentInternal = get() as HomeDomainComponentInternal
}
