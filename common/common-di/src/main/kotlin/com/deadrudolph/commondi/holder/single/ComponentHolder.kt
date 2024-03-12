package com.deadrudolph.commondi.holder.single

import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.ClearedComponentHolder

/**
 *
 * Allows to get, set and clear the component. If the component doesn't exist,
 * the new one will be created
 */
abstract class ComponentHolder<Component : DIComponent> :
    BaseComponentHolder<Component>,
    ClearedComponentHolder {

    private var component: Component? = null

    override fun get(): Component {
        return component ?: build().also {
            component = it
        }
    }

    override fun set(component: Component) {
        this.component = component
    }

    protected abstract fun build(): Component

    override fun clear() {
        component = null
    }
}
