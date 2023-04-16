package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import kotlin.math.roundToInt

@Composable
internal fun HandlePopup(
    position: Offset,
    handleReferencePoint: HandleReferencePoint,
    content: @Composable () -> Unit
) {
    val intOffset = IntOffset(position.x.roundToInt(), position.y.roundToInt())

    val popupPositioner = remember(handleReferencePoint, intOffset) {
        HandlePositionProvider(handleReferencePoint, intOffset)
    }

    Popup(
        popupPositionProvider = popupPositioner,
        properties = PopupProperties(
            excludeFromSystemGesture = true,
            clippingEnabled = false
        ),
        content = content
    )

}

internal enum class HandleReferencePoint {
    TopLeft,
    TopRight,
    TopMiddle
}

internal class HandlePositionProvider(
    private val handleReferencePoint: HandleReferencePoint,
    private val offset: IntOffset
) : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return when (handleReferencePoint) {
            HandleReferencePoint.TopLeft ->
                IntOffset(
                    x = anchorBounds.left + offset.x,
                    y = anchorBounds.top + offset.y
                )
            HandleReferencePoint.TopRight ->
                IntOffset(
                    x = anchorBounds.left + offset.x - popupContentSize.width,
                    y = anchorBounds.top + offset.y
                )
            HandleReferencePoint.TopMiddle ->
                IntOffset(
                    x = anchorBounds.left + offset.x - popupContentSize.width / 2,
                    y = anchorBounds.top + offset.y
                )
        }
    }
}