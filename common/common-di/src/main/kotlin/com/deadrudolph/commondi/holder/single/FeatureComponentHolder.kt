package com.deadrudolph.commondi.holder.single

import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.ClearedComponentHolder
import java.lang.ref.WeakReference

/**
 *
 * Холдер компонента с автоматической очисткой. Позволяет получить компонент. Если компонента нет то создается новый.
 *
 * !!!ВАЖНО
 * Не использовать этот холдер, если в вашем компоненте имеется какой либо скоуп (@Singleton, свой кастомный и др.).
 * Так как данный холдер может очистится при отсутствии ссылок на компонент, мы не можем гарантировать существование синглтонов.
 * Для компонентов имеющих скоуп используйте com.puls.commondi.holder.single.ComponentHolder
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
     * Создает слабую ссылку на компонент
     *
     * <p class="caution"><strong>Внимание:</strong> Метод set не создает
     * сильной ссылки на компонет. Вы должны хранить ссылку на компоненту в клиентском коде.
     * Метод должен использоваться только в тестах для подмены на тестовые сущности</p>
     *
     * @param component Компонента DI
     */
    override fun set(component: Component) {
        this.component = WeakReference(component)
    }

    protected abstract fun build(): Component

    override fun clear() {
        component = null
    }
}
