package com.deadrudolph.home.di.component

import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.commondi.module.CommonDiModule
import com.deadrudolph.home.di.dependencies.DependenciesImpl
import com.deadrudolph.home.di.module.ViewModelModule
import com.deadrudolph.home_domain.domain.usecase.users.GetAllUsersUseCase
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

    interface Dependencies {
        val getAllUsersUseCase: GetAllUsersUseCase
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
