package com.deadrudolph.tuner.di.dependencies

import android.content.Context
import com.deadrudolph.commondi.component.app.ApplicationComponentHolder
import com.deadrudolph.tuner.di.component.TunerComponentInternal

class DependenciesImpl : TunerComponentInternal.Dependencies {

    override val context: Context = ApplicationComponentHolder.get().context()
}
