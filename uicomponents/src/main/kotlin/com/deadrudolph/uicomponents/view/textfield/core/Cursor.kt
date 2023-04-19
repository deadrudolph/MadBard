package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import com.deadrudolph.uicomponents.view.textfield.core.constants.CursorHandleHeight
import com.deadrudolph.uicomponents.view.textfield.core.constants.CursorHandleWidth
import com.deadrudolph.uicomponents.view.textfield.extension.drawCursorHandle

@Composable
internal fun CursorHandle(
    handlePosition: Offset,
    modifier: Modifier,
    content: @Composable (() -> Unit)?
) {
    HandlePopup(
        position = handlePosition,
        handleReferencePoint = HandleReferencePoint.TopMiddle
    ) {
        if (content == null) {
            DefaultCursorHandle(modifier = modifier)
        } else {
            content()
        }
    }
}

@Composable
/*@VisibleForTesting*/
internal fun DefaultCursorHandle(modifier: Modifier) {
    Spacer(
        modifier
            .size(CursorHandleWidth, CursorHandleHeight)
            .drawCursorHandle())
}