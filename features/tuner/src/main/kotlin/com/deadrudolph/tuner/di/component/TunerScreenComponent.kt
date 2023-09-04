package com.deadrudolph.tuner.di.component

import com.deadrudolph.tuner.view.tuner.TunerScreen


internal interface TunerScreenComponent {

    fun inject(screen: TunerScreen)
}
