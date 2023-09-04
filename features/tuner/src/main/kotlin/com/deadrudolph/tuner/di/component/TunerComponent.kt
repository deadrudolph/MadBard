package com.deadrudolph.tuner.di.component

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.commondi.module.CommonDiModule
import com.deadrudolph.tuner.di.dependencies.DependenciesImpl
import com.deadrudolph.tuner.di.module.ManagerModule
import com.deadrudolph.tuner.di.module.ViewModelModule
import dagger.Component
import javax.inject.Singleton

interface TunerComponent : DIComponent

@Singleton
@Component(
    modules = [
        CommonDiModule::class,
        ViewModelModule::class,
        ManagerModule::class
    ],
    dependencies = [
        TunerComponentInternal.Dependencies::class,
    ]
)
internal interface TunerComponentInternal :
    TunerComponent,
    TunerScreenComponent {

    fun getViewModelFactory(): ViewModelProvider.Factory

    interface Dependencies {
        val context: Context
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): TunerComponentInternal
    }
}

object TunerComponentHolder : FeatureComponentHolder<TunerComponent>() {

    override fun build(): TunerComponent {

        return DaggerTunerComponentInternal.factory().create(
            DependenciesImpl(),
        )
    }

    internal fun getInternal(): TunerComponentInternal = get() as TunerComponentInternal
}
