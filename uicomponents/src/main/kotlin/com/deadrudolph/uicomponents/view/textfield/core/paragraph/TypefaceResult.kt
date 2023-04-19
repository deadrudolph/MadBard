package com.deadrudolph.uicomponents.view.textfield.core.paragraph

import androidx.compose.runtime.State

internal sealed interface TypefaceResult : State<Any> {
    val cacheable: Boolean
    // Immutable results present as State, but don't trigger a read observer
    class Immutable(
        override val value: Any,
        override val cacheable: Boolean = true
    ) : TypefaceResult
}