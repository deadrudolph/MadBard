package com.deadrudolph.commondi.component.app

import com.deadrudolph.commondi.holder.single.DataComponentHolder

object ApplicationComponentHolder :
    DataComponentHolder<ApplicationComponent, ApplicationComponentDependencies>() {

    override fun build(data: ApplicationComponentDependencies): ApplicationComponent {
        return DaggerApplicationComponentInternal.factory().create(context = data.context)

    }
}
