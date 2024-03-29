package com.deadrudolph.feature_builder.di.component

import com.deadrudolph.feature_builder.presentation.ui.screen.song_builder.SongBuilderScreen

internal interface SongBuilderScreenComponent {

    fun inject(screen: SongBuilderScreen)
}