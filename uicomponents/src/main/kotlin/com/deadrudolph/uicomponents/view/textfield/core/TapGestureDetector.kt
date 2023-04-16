package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.gestures.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.unit.Density
import com.deadrudolph.uicomponents.view.textfield.extension.consume
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex

/**
 * [detectTapGestures]'s implementation of [PressGestureScope].
 */
private class PressGestureScopeImpl(
    density: Density
) : PressGestureScope, Density by density {
    private var isReleased = false
    private var isCanceled = false
    private val mutex = Mutex(locked = false)

    /**
     * Called when a gesture has been canceled.
     */
    fun cancel() {
        isCanceled = true
        mutex.unlock()
    }

    /**
     * Called when all pointers are up.
     */
    fun release() {
        isReleased = true
        mutex.unlock()
    }

    /**
     * Called when a new gesture has started.
     */
    suspend fun reset() {
        mutex.lock()
        isReleased = false
        isCanceled = false
    }

    override suspend fun awaitRelease() {
        if (!tryAwaitRelease()) {
            throw GestureCancellationException("The press gesture was canceled.")
        }
    }

    override suspend fun tryAwaitRelease(): Boolean {
        if (!isReleased && !isCanceled) {
            mutex.lock()
            mutex.unlock()
        }
        return isReleased
    }
}

internal suspend fun PointerInputScope.detectTapAndPress(
    onPress: suspend PressGestureScope.(Offset) -> Unit = NoPressGesture,
    onTap: ((Offset) -> Unit)? = null
) {
    val pressScope = PressGestureScopeImpl(this)
    coroutineScope {
        awaitEachGesture {
            launch {
                pressScope.reset()
            }

            val down = awaitFirstDown().also { it.consume() }

            if (onPress !== NoPressGesture) {
                launch {
                    pressScope.onPress(down.position)
                }
            }

            val up = waitForUpOrCancellation()
            if (up == null) {
                launch {
                    pressScope.cancel() // tap-up was canceled
                }
            } else {
                up.consume()
                launch {
                    pressScope.release()
                }
                onTap?.invoke(up.position)
            }
        }
    }
}

private val NoPressGesture: suspend PressGestureScope.(Offset) -> Unit = { }
