package com.deadrudolph.feature_player.di.component

import androidx.lifecycle.ViewModelProvider
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.commondi.module.CommonDiModule
import com.deadrudolph.feature_player.di.dependencies.DependenciesImpl
import com.deadrudolph.feature_player.di.module.ViewModelModule
import com.deadrudolph.feature_player_domain.domain.usecase.GetSongByIdUseCase
import dagger.Component

interface PlayerComponent : DIComponent

@Component(
    modules = [
        CommonDiModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        PlayerComponentInternal.Dependencies::class,
    ]
)
internal interface PlayerComponentInternal :
    PlayerComponent,
    PlayerScreenComponent {

    fun getViewModelFactory(): ViewModelProvider.Factory

    interface Dependencies {
        val getSongByIdUseCase: GetSongByIdUseCase
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): PlayerComponentInternal
    }
}

object PlayerComponentHolder : FeatureComponentHolder<PlayerComponent>() {

    override fun build(): PlayerComponent {

        return DaggerPlayerComponentInternal.factory().create(
            DependenciesImpl(),
        )
    }

    internal fun getInternal(): PlayerComponentInternal = get() as PlayerComponentInternal
}