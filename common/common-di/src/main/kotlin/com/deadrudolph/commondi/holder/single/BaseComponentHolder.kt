package com.deadrudolph.commondi.holder.single

import com.deadrudolph.commondi.component.base.DIComponent

/**
 *
 * Represents basic behaviour for component holders
 */
interface BaseComponentHolder<Component : DIComponent> {

    fun get(): Component

    fun set(component: Component)
}
