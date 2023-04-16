package com.deadrudolph.commondatabase.di.dependencies

import android.content.Context
import com.deadrudolph.commondatabase.di.component.DatabaseComponentInternal
import com.deadrudolph.commondi.component.app.ApplicationComponentHolder

class DependenciesImpl : DatabaseComponentInternal.Dependencies {

    override val context: Context = ApplicationComponentHolder.get().context()
}