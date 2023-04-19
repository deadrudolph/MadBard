package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.ResolvedTextDirection
import com.deadrudolph.uicomponents.view.textfield.core.HandleReferencePoint.TopLeft
import com.deadrudolph.uicomponents.view.textfield.core.HandleReferencePoint.TopRight
import com.deadrudolph.uicomponents.view.textfield.core.constants.HandleHeight
import com.deadrudolph.uicomponents.view.textfield.core.constants.HandleWidth
import com.deadrudolph.uicomponents.view.textfield.core.constants.SelectionHandleInfoKey
import com.deadrudolph.uicomponents.view.textfield.extension.drawSelectionHandle
import com.deadrudolph.uicomponents.view.textfield.extension.isLeft

@Composable
internal fun SelectionHandle(
    position: Offset,
    isStartHandle: Boolean,
    direction: ResolvedTextDirection,
    handlesCrossed: Boolean,
    modifier: Modifier,
    content: @Composable (() -> Unit)?
) {
    val isLeft = isLeft(isStartHandle, direction, handlesCrossed)
    // The left selection handle's top right is placed at the given position, and vice versa.
    val handleReferencePoint = if (isLeft) {
        TopRight
    } else {
        TopLeft
    }

    HandlePopup(
        position = position,
        handleReferencePoint = handleReferencePoint
    ) {
        if (content == null) {
            DefaultSelectionHandle(
                modifier = modifier
                    .semantics {
                        this[SelectionHandleInfoKey] =
                            SelectionHandleInfo(
                                handle = if (isStartHandle) {
                                    Handle.SelectionStart
                                } else {
                                    Handle.SelectionEnd
                                },
                                position = position
                            )
                    },
                isStartHandle = isStartHandle,
                direction = direction,
                handlesCrossed = handlesCrossed
            )
        } else {
            content()
        }
    }
}

@Composable
/*@VisibleForTesting*/
internal fun DefaultSelectionHandle(
    modifier: Modifier,
    isStartHandle: Boolean,
    direction: ResolvedTextDirection,
    handlesCrossed: Boolean
) {
    Spacer(
        modifier
            .size(HandleWidth, HandleHeight)
            .drawSelectionHandle(isStartHandle, direction, handlesCrossed)
    )
}