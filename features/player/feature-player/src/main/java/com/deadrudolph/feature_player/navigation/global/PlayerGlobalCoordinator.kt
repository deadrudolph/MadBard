package com.deadrudolph.feature_player.navigation.global

import cafe.adriel.voyager.core.registry.screenModule
import com.deadrudolph.feature_player.ui.screen.player.PlayerScreen
import com.deadrudolph.navigation.screen.SharedScreen

val featurePlayerScreenModule = screenModule {
    register<SharedScreen.PlayerScreen> { playerScreen ->
        PlayerScreen(playerScreen.songItemId)
    }
}