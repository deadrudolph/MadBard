package com.deadrudolph.feature_builder.di.module

import androidx.lifecycle.ViewModel
import com.deadrudolph.commondi.util.ViewModelKey
import com.deadrudolph.feature_builder.presentation.ui.screen.song_builder.SongBuilderViewModel
import com.deadrudolph.feature_builder.presentation.ui.screen.song_builder.SongBuilderViewModelImpl
import com.deadrudolph.feature_builder.presentation.ui.screen.song_import.SongImportViewModel
import com.deadrudolph.feature_builder.presentation.ui.screen.song_import.SongImportViewModelImpl
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
internal interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SongBuilderViewModel::class)
    fun bindSongBuilderViewModel(viewModel: SongBuilderViewModelImpl): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SongImportViewModel::class)
    fun bindSongImportViewModel(viewModel: SongImportViewModelImpl): ViewModel
}