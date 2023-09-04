package com.deadrudolph.tuner.ktx

import timber.log.Timber

fun logError(error: Throwable) = Timber.e(error.message)
