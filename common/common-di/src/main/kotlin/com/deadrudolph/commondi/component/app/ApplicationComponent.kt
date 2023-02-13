package com.deadrudolph.commondi.component.app

import android.content.Context
import com.deadrudolph.commondi.component.base.DIComponent
import dagger.BindsInstance
import dagger.Component

interface ApplicationComponent : DIComponent {

    fun context(): Context
}

@Component()
internal interface ApplicationComponentInternal : ApplicationComponent {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}
