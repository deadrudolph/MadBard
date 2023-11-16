package com.deadrudolph.navigation.screen

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen : ScreenProvider {
    object AuthorizationScreen : SharedScreen()
    object HomeScreen : SharedScreen()

    object TunerScreen : SharedScreen()
    class PlayerScreen(val songItemId: String) : SharedScreen()
}
