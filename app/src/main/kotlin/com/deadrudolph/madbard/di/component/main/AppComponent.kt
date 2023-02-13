package com.deadrudolph.madbard.di.component.main

import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import dagger.Component

interface AppComponent : DIComponent

@Component()
internal interface AppComponentInternal :
    AppComponent,
    AppActivityComponent {

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
