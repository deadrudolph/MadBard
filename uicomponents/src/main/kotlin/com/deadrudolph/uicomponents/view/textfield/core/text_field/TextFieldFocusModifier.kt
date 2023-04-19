package com.deadrudolph.uicomponents.view.textfield.core.text_field

import android.view.InputDevice
import android.view.KeyEvent

import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.input.key.*

internal fun Modifier.interceptDPadAndMoveFocus(
    state: NewTextFieldState,
    focusManager: FocusManager
): Modifier {
    return this
        .onPreviewKeyEvent { keyEvent ->
            // If direction keys from virtual alphabetic keyboard are used, propagate the input
            val device = keyEvent.nativeKeyEvent.device ?: return@onPreviewKeyEvent false
            if (device.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC && device.isVirtual) {
                return@onPreviewKeyEvent false
            }

            // Handle only the key press events, ignore key release events
            if (keyEvent.type != KeyEventType.KeyDown) return@onPreviewKeyEvent false

            when (keyEvent.key.nativeKeyCode) {
                KeyEvent.KEYCODE_DPAD_UP -> focusManager.moveFocus(FocusDirection.Up)
                KeyEvent.KEYCODE_DPAD_DOWN -> focusManager.moveFocus(FocusDirection.Down)
                KeyEvent.KEYCODE_DPAD_LEFT -> focusManager.moveFocus(FocusDirection.Left)
                KeyEvent.KEYCODE_DPAD_RIGHT -> focusManager.moveFocus(FocusDirection.Right)
                KeyEvent.KEYCODE_DPAD_CENTER -> {
                    // Enable keyboard on center key press
                    state.inputSession?.showSoftwareKeyboard()
                    true
                }
                else -> false
            }
        }
}