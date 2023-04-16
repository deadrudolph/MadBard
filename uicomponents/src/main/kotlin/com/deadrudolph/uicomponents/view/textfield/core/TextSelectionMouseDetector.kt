package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.input.pointer.*
import androidx.compose.ui.platform.ViewConfiguration
import com.deadrudolph.uicomponents.view.textfield.extension.fastAll

internal const val ClicksSlop = 100.0

internal suspend fun PointerInputScope.mouseSelectionDetector(
    observer: MouseSelectionObserver
) {
    awaitEachGesture {
        val clicksCounter = ClicksCounter(viewConfiguration)
        while (true) {
            val down = awaitMouseEventDown()
            clicksCounter.update(down)
            val downChange = down.changes[0]
            if (down.isShiftPressed) {
                val started = observer.onExtend(downChange.position)
                if (started) {
                    downChange.consume()
                    drag(downChange.id) {
                        if (observer.onExtendDrag(it.position)) {
                            it.consume()
                        }
                    }
                }
            } else {
                val selectionMode = when (clicksCounter.clicks) {
                    1 -> NewSelectionAdjustment.None
                    2 -> NewSelectionAdjustment.Word
                    else -> NewSelectionAdjustment.Paragraph
                }
                val started = observer.onStart(downChange.position, selectionMode)
                if (started) {
                    downChange.consume()
                    drag(downChange.id) {
                        if (observer.onDrag(it.position, selectionMode)) {
                            it.consume()
                        }
                    }
                }
            }
        }
    }
}

private class ClicksCounter(
    private val viewConfiguration: ViewConfiguration
) {
    var clicks = 0
    var prevClick: PointerInputChange? = null
    fun update(event: PointerEvent) {
        val currentPrevClick = prevClick
        val newClick = event.changes[0]
        if (currentPrevClick != null &&
            timeIsTolerable(currentPrevClick, newClick) &&
            positionIsTolerable(currentPrevClick, newClick)
        ) {
            clicks += 1
        } else {
            clicks = 1
        }
        prevClick = newClick
    }

    fun timeIsTolerable(prevClick: PointerInputChange, newClick: PointerInputChange): Boolean {
        val diff = newClick.uptimeMillis - prevClick.uptimeMillis
        return diff < viewConfiguration.doubleTapTimeoutMillis
    }

    fun positionIsTolerable(prevClick: PointerInputChange, newClick: PointerInputChange): Boolean {
        val diff = newClick.position - prevClick.position
        return diff.getDistance() < ClicksSlop
    }
}

private suspend fun AwaitPointerEventScope.awaitMouseEventDown(): PointerEvent {
    var event: PointerEvent
    do {
        event = awaitPointerEvent(PointerEventPass.Main)
    } while (
        !(
                event.buttons.isPrimaryPressed && event.changes.fastAll {
                    it.type == PointerType.Mouse && it.changedToDown()
                }
                )
    )
    return event
}