package com.deadrudolph.madbard.di.component.main

import androidx.lifecycle.ViewModelProvider
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.commondi.module.CommonDiModule
import com.deadrudolph.madbard.di.module.ViewModelModule
import dagger.Component

interface AppComponent : DIComponent


@Component(
    modules = [
        ViewModelModule::class,
        CommonDiModule::class
    ]
)
internal interface AppComponentInternal :
    AppComponent,
    AppActivityComponent {

    fun getViewModelFactory(): ViewModelProvider.Factory

    @Component.Factory
    interface Factory {
        fun create(): AppComponentInternal
    }
}

object AppComponentHolder : FeatureComponentHolder<AppComponent>() {

    override fun build(): AppComponent {

        return DaggerAppComponentInternal.factory().create()
    }

    internal fun getInternal(): AppComponentInternal = get() as AppComponentInternal
}
