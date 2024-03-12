package com.deadrudolph.commondi.holder.single

import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.ClearedComponentHolder
import java.lang.ref.WeakReference

/**
 *
 * Holder with automatic disposing of the component
 *
 * !!! IMPORTANT
 * DO NOT use this holder for components which contain some scoped dependencies.
 */
abstract class FeatureComponentHolder <Component : DIComponent> :
    BaseComponentHolder<Component>,
    ClearedComponentHolder {

    private var component: WeakReference<Component>? = null

    override fun get(): Component {
        return component?.get() ?: build().also {
            component = WeakReference(it)
        }
    }

    /**
     * Created weak reference with a component
     */
    override fun set(component: Component) {
        this.component = WeakReference(component)
    }

    protected abstract fun build(): Component

    override fun clear() {
        component = null
    }
}
