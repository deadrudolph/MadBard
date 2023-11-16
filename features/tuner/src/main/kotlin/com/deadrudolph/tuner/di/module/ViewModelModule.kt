package com.deadrudolph.tuner.di.module

import androidx.lifecycle.ViewModel
import com.deadrudolph.commondi.util.ViewModelKey
import com.deadrudolph.tuner.view.tuner.TunerViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(TunerViewModel::class)
    fun bindSongBuilderViewModel(viewModel: TunerViewModel): ViewModel
}
