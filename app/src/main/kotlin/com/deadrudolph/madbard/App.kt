package com.deadrudolph.madbard

import android.app.Application
import android.content.Context
import cafe.adriel.voyager.core.registry.ScreenRegistry
import com.deadrudolph.commondi.component.app.ApplicationComponentDependencies
import com.deadrudolph.commondi.component.app.ApplicationComponentHolder
import com.deadrudolph.feature_player.navigation.global.featurePlayerScreenModule
import com.deadrudolph.home.navigation.global.featureHomeScreenModule
import com.deadrudolph.tuner.navigation.global.featureTunerScreenModule
import timber.log.Timber

internal class App : Application() {

    override fun onCreate() {
        super.onCreate()
        initComponent()
        initGlobalNavigation()
        initTimber()
    }

    private fun initComponent() {
        ApplicationComponentHolder.init(
            object : ApplicationComponentDependencies {
                override val context: Context = this@App
            }
        )
    }

    private fun initGlobalNavigation() {
        ScreenRegistry {
            featureHomeScreenModule()
            featurePlayerScreenModule()
            featureTunerScreenModule()
        }
    }

    private fun initTimber() {
        Timber.plant(Timber.DebugTree())
    }
}
