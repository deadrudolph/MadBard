package com.deadrudolph.feature_builder.di.module

import androidx.lifecycle.ViewModel
import com.deadrudolph.commondi.util.ViewModelKey
import com.deadrudolph.feature_builder.presentation.ui.screen.SongBuilderViewModel
import com.deadrudolph.feature_builder.presentation.ui.screen.SongBuilderViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SongBuilderViewModel::class)
    fun bindHomeViewModel(viewModel: SongBuilderViewModelImpl): ViewModel
}