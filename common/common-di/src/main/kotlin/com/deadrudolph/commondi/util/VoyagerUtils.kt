package com.deadrudolph.commondi.util

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.androidx.AndroidScreenLifecycleOwner
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleProvider
import cafe.adriel.voyager.core.screen.Screen

@Composable
public inline fun <reified T : ViewModel> Screen.getDaggerViewModel(
    viewModelProviderFactory: ViewModelProvider.Factory,
    isSharedViewModel: Boolean = false
): T {
    val context = LocalContext.current
    return remember(key1 = T::class) {
        val activity = context.componentActivity
        val lifecycleOwner =
            (this as? ScreenLifecycleProvider)?.getLifecycleOwner() as? AndroidScreenLifecycleOwner
        val viewModelStore = if (isSharedViewModel) activity.viewModelStore
        else lifecycleOwner?.viewModelStore ?: activity.viewModelStore
        val provider = ViewModelProvider(store = viewModelStore, factory = viewModelProviderFactory)
        provider[T::class.java]
    }
}

internal inline fun <reified T> findOwner(context: Context): T? {
    var innerContext = context
    while (innerContext is ContextWrapper) {
        if (innerContext is T) {
            return innerContext
        }
        innerContext = innerContext.baseContext
    }
    return null
}

@PublishedApi
internal val Context.componentActivity: ComponentActivity
    get() = findOwner<ComponentActivity>(this)
        ?: error("Context must be a androidx.activity.ComponentActivity. Current is $this")
