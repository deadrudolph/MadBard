package com.example.feature_builder_domain.di.component

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.example.feature_builder_domain.di.DependenciesImpl
import com.example.feature_builder_domain.di.module.RepositoryModule
import com.example.feature_builder_domain.di.module.UseCaseModule
import com.example.feature_builder_domain.domain.usecase.SaveSongUseCase
import dagger.Component

interface SongBuilderDomainComponent : DIComponent {

    fun saveSongsUseCase(): SaveSongUseCase

}

@Component(
    modules = [
        RepositoryModule::class,
        UseCaseModule::class
    ],

    dependencies = [
        SongBuilderDomainComponentInternal.Dependencies::class,
    ]
)
internal interface SongBuilderDomainComponentInternal : SongBuilderDomainComponent {

    interface Dependencies {
        val songsDao: SongsDao
        val songItemToSongEntityMapper: SongItemToSongEntityMapper
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): SongBuilderDomainComponentInternal
    }
}

object SongBuilderDomainComponentHolder : FeatureComponentHolder<SongBuilderDomainComponent>() {

    override fun build(): SongBuilderDomainComponent {

        return DaggerSongBuilderDomainComponentInternal.factory().create(
            DependenciesImpl()
        )
    }

    internal fun getInternal(): SongBuilderDomainComponentInternal =
        get() as SongBuilderDomainComponentInternal
}