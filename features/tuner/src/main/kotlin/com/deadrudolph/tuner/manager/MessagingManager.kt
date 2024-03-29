package com.deadrudolph.tuner.manager

import android.content.Context
import androidx.annotation.StringRes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class MessagingManager @Inject constructor(
    private val context: Context
) {

    private val _state by lazy { MutableStateFlow<String?>(null) }
    val state by lazy { _state.asStateFlow() }

    fun send(@StringRes messageRes: Int) {
        _state.value = context.getString(messageRes)
    }

    fun consume() {
        _state.value = null
    }
}
