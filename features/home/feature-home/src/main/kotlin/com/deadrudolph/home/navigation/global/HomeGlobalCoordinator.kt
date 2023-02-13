package com.deadrudolph.home.navigation.global

import cafe.adriel.voyager.core.registry.screenModule
import com.deadrudolph.home.presentation.ui.screen.home.HomeScreen
import com.deadrudolph.navigation.screen.SharedScreen

val featureHomeScreenModule = screenModule {
    register<SharedScreen.HomeScreen> {
        HomeScreen()
    }
}