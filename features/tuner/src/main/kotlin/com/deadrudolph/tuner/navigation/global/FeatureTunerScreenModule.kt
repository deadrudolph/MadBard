package com.deadrudolph.tuner.navigation.global

import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.core.registry.screenModule
import com.deadrudolph.navigation.screen.SharedScreen
import com.deadrudolph.tuner.view.tuner.TunerScreen

val featureTunerScreenModule = screenModule {
    register<SharedScreen.TunerScreen> {
        TunerScreen()
    }
}
