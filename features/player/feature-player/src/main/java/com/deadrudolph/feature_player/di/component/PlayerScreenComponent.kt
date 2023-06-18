package com.deadrudolph.feature_player.di.component

import com.deadrudolph.feature_player.ui.screen.player.PlayerScreen
import dagger.Component

@Component
internal interface PlayerScreenComponent {

    fun inject(screen: PlayerScreen)
}