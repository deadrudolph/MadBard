package com.deadrudolph.home.di.component

import androidx.lifecycle.ViewModelProvider
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.commondi.module.CommonDiModule
import com.deadrudolph.home.di.dependencies.DependenciesImpl
import com.deadrudolph.home.di.module.ViewModelModule
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import dagger.Component

interface HomeComponent : DIComponent

@Component(
    modules = [
        CommonDiModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        HomeComponentInternal.Dependencies::class,
    ]
)
internal interface HomeComponentInternal :
    HomeComponent,
    HomeScreenComponent {

    fun getViewModelFactory(): ViewModelProvider.Factory

    interface Dependencies {
        val getAllSongsUseCase: GetAllSongsUseCase
        val saveSongsUseCase: SaveSongsUseCase
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): HomeComponentInternal
    }
}

object HomeComponentHolder : FeatureComponentHolder<HomeComponent>() {

    override fun build(): HomeComponent {

        return DaggerHomeComponentInternal.factory().create(
            DependenciesImpl(),
        )
    }

    internal fun getInternal(): HomeComponentInternal = get() as HomeComponentInternal
}
