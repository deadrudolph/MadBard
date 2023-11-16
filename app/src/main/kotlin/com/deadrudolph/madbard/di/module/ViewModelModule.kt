package com.deadrudolph.madbard.di.module

import androidx.lifecycle.ViewModel
import com.deadrudolph.commondi.util.ViewModelKey
import com.deadrudolph.madbard.presentation.ui.activity.MainActivityViewModel
import com.deadrudolph.madbard.presentation.ui.activity.MainActivityViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainActivityViewModel::class)
    fun bindHomeViewModel(viewModel: MainActivityViewModelImpl): ViewModel
}