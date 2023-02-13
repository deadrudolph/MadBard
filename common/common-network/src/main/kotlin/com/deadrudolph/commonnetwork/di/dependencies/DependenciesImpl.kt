package com.deadrudolph.commonnetwork.di.dependencies

import android.content.Context
import com.deadrudolph.commondi.component.app.ApplicationComponentHolder
import com.deadrudolph.commonnetwork.di.component.NetworkComponentInternal

internal class DependenciesImpl : NetworkComponentInternal.Dependencies {

    override val context: Context
        get() = ApplicationComponentHolder.get().context()
}