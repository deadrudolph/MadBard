package com.deadrudolph.feature_player.utils

import android.os.CountDownTimer

class CountDownTimerFacade(
    millisInFuture: Long,
    private val onValueChanged:(Long) -> Unit,
    private val onFinished: () -> Unit
): CountDownTimer(
    millisInFuture, 1000L
) {
    override fun onTick(millisUntilFinished: Long) {
        onValueChanged(millisUntilFinished)
    }

    override fun onFinish() {
        onFinished()
    }
}