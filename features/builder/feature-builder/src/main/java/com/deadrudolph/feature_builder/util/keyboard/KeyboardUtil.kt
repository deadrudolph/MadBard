package com.deadrudolph.feature_builder.util.keyboard

import android.graphics.Rect
import android.view.ViewTreeObserver
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.pxToDp

@Composable
fun keyboardHeightState(): State<KeyBoardState> {
    val keyboardState = remember {
        mutableStateOf(
            KeyBoardState(false, 0.dp)
        )
    }

    val view = LocalView.current
    DisposableEffect(view) {
        val onGlobalListener = ViewTreeObserver.OnGlobalLayoutListener {
            val rect = Rect()
            view.rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = view.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            keyboardState.value = if (
                keypadHeight > screenHeight * 0.15
            ) KeyBoardState(
                true,
                keypadHeight.pxToDp.dp
            )
            else KeyBoardState(
                false,
                0.dp
            )

        }
        view.viewTreeObserver.addOnGlobalLayoutListener(onGlobalListener)

        onDispose {
            view.viewTreeObserver.removeOnGlobalLayoutListener(onGlobalListener)
        }
    }

    return keyboardState
}