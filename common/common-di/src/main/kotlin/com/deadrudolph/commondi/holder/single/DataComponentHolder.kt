package com.deadrudolph.commondi.holder.single

import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.ClearedComponentHolder

/**
 *
 * Позволяет получить компонент который требует на вход какие либо данные.
 */
abstract class DataComponentHolder<Component : DIComponent, Data : Any> :
    BaseComponentHolder<Component>,
    ClearedComponentHolder {

    private var component: Component? = null

    override fun get(): Component {
        return requireNotNull(component) { "${javaClass.simpleName} — component not found" }
    }

    fun init(data: Data) {
        component ?: build(data).also { component = it }
    }

    override fun set(component: Component) {
        this.component = component
    }

    override fun clear() {
        component = null
    }

    protected abstract fun build(data: Data): Component
}
