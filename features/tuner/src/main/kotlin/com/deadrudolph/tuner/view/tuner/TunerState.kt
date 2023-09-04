package com.deadrudolph.tuner.view.tuner

import com.deadrudolph.tuner.model.settings.Settings
import com.deadrudolph.tuner.model.tuner.Tuning

data class TunerState(
    val settings: Settings,
    val tuning: Tuning = Tuning(),
    val message: String? = null,
    val hasRequiredPermissions: Boolean = false,
    val isBillingSupported: Boolean = false
)
