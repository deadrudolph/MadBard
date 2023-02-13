package com.deadrudolph.commondi.module

import androidx.lifecycle.ViewModelProvider
import com.deadrudolph.commondi.util.ViewModelFactory
import dagger.Binds
import dagger.Module

@Module
interface CommonDiModule {

    @Binds
    fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory
}
