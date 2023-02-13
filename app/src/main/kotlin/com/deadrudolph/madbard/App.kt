package com.deadrudolph.madbard

import android.app.Application
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.deadrudolph.home.navigation.global.featureHomeScreenModule
import timber.log.Timber

internal class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initGlobalNavigation()
        initTimber()
    }

    private fun initGlobalNavigation() {
        ScreenRegistry {
            featureHomeScreenModule()
        }
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}