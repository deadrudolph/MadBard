package com.deadrudolph.commondi.holder.single

import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.ClearedComponentHolder

/**
 *
 * Простой компонент холдер, компонент которого нужно передать в метод set()
 */
abstract class SimpleComponentHolder<Component : DIComponent> :
    BaseComponentHolder<Component>,
    ClearedComponentHolder {

    private var component: Component? = null

    override fun get(): Component {
        return requireNotNull(component) { "${javaClass.simpleName} — component not found" }
    }

    override fun set(component: Component) {
        this.component = component
    }

    override fun clear() {
        this.component = null
    }
}
