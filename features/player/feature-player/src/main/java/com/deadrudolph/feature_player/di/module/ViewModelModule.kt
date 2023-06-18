package com.deadrudolph.feature_player.di.module

import androidx.lifecycle.ViewModel
import com.deadrudolph.commondi.util.ViewModelKey
import com.deadrudolph.feature_player.ui.screen.player.PlayerScreenViewModel
import com.deadrudolph.feature_player.ui.screen.player.PlayerScreenViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(PlayerScreenViewModel::class)
    fun bindHomeViewModel(viewModel: PlayerScreenViewModelImpl): ViewModel
}