package com.deadrudolph.feature_player_domain.di.component

import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.feature_player_domain.di.dependencies.DependenciesImpl
import com.deadrudolph.feature_player_domain.di.module.RepositoryModule
import com.deadrudolph.feature_player_domain.di.module.UseCaseModule
import com.deadrudolph.feature_player_domain.domain.usecase.GetSongByIdUseCase
import dagger.Component

interface PlayerDomainComponent : DIComponent {

    fun getSongByIdUseCase(): GetSongByIdUseCase
}

@Component(
    modules = [
        RepositoryModule::class,
        UseCaseModule::class
    ],

    dependencies = [
        PlayerDomainComponentInternal.Dependencies::class,
    ]
)
internal interface PlayerDomainComponentInternal : PlayerDomainComponent {

    interface Dependencies {
        val songsDao: SongsDao
        val songEntityToSongItemMapper: SongEntityToSongItemMapper
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): PlayerDomainComponentInternal
    }
}

object PlayerDomainComponentHolder : FeatureComponentHolder<PlayerDomainComponent>() {

    override fun build(): PlayerDomainComponent {

        return DaggerPlayerDomainComponentInternal.factory().create(
            DependenciesImpl()
        )
    }

    internal fun getInternal(): PlayerDomainComponentInternal = get() as PlayerDomainComponentInternal
}