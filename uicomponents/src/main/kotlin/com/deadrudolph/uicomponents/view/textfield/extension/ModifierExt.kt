package com.deadrudolph.uicomponents.view.textfield.extension

import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.style.ResolvedTextDirection
import com.deadrudolph.uicomponents.view.textfield.core.*
import com.deadrudolph.uicomponents.view.textfield.core.MouseSelectionObserver
import com.deadrudolph.uicomponents.view.textfield.core.TextDragObserver
import com.deadrudolph.uicomponents.view.textfield.core.detectDragGesturesAfterLongPressWithObserver
import com.deadrudolph.uicomponents.view.textfield.core.mouseSelectionDetector
import com.deadrudolph.uicomponents.view.textfield.core.semantic.SemanticsModifierCore
import com.deadrudolph.uicomponents.view.textfield.core.semantic.SemanticsPropertyReceiver
import kotlinx.coroutines.launch

@Suppress("ModifierInspectorInfo")
internal fun Modifier.drawCursorHandle() = composed {
    val handleColor = LocalTextSelectionColors.current.handleColor
    this.then(
        Modifier.drawWithCache {
            // Cursor handle is the same as a SelectionHandle rotated 45 degrees clockwise.
            val radius = size.width / 2f
            val imageBitmap = createHandleImage(radius = radius)
            val colorFilter = ColorFilter.tint(handleColor)
            onDrawWithContent {
                drawContent()
                withTransform({
                    translate(left = radius)
                    rotate(degrees = 45f, pivot = Offset.Zero)
                }) {
                    drawImage(image = imageBitmap, colorFilter = colorFilter)
                }
            }
        }
    )
}

@Suppress("ModifierInspectorInfo")
internal fun Modifier.animatedSelectionMagnifier(
    magnifierCenter: () -> Offset,
    platformMagnifier: (animatedCenter: () -> Offset) -> Modifier
): Modifier = composed {
    val animatedCenter by rememberAnimatedMagnifierPosition(targetCalculation = magnifierCenter)
    return@composed platformMagnifier { animatedCenter }
}

@Suppress("ModifierInspectorInfo")
internal fun Modifier.drawSelectionHandle(
    isStartHandle: Boolean,
    direction: ResolvedTextDirection,
    handlesCrossed: Boolean
) = composed {
    val handleColor = LocalTextSelectionColors.current.handleColor
    this.then(
        Modifier.drawWithCache {
            val radius = size.width / 2f
            val handleImage = createHandleImage(radius)
            val colorFilter = ColorFilter.tint(handleColor)
            onDrawWithContent {
                drawContent()
                val isLeft = isLeft(isStartHandle, direction, handlesCrossed)
                if (isLeft) {
                    // Flip the selection handle horizontally.
                    scale(scaleX = -1f, scaleY = 1f) {
                        drawImage(
                            image = handleImage,
                            colorFilter = colorFilter
                        )
                    }
                } else {
                    drawImage(
                        image = handleImage,
                        colorFilter = colorFilter
                    )
                }
            }
        }
    )
}

internal fun Modifier.mouseDragGestureDetector(
    observer: MouseSelectionObserver,
    enabled: Boolean
) = if (enabled) Modifier.pointerInput(observer) {
    mouseSelectionDetector(observer)
} else this

internal fun Modifier.longPressDragGestureFilter(
    observer: TextDragObserver,
    enabled: Boolean
) = if (enabled) {
    this.pointerInput(observer) { detectDragGesturesAfterLongPressWithObserver(observer) }
} else {
    this
}

@Suppress("ModifierInspectorInfo")
internal fun Modifier.tapPressTextFieldModifier(
    interactionSource: MutableInteractionSource?,
    enabled: Boolean = true,
    onTap: (Offset) -> Unit
): Modifier = if (enabled) composed {
    val scope = rememberCoroutineScope()
    val pressedInteraction = remember { mutableStateOf<PressInteraction.Press?>(null) }
    val onTapState = rememberUpdatedState(onTap)
    DisposableEffect(interactionSource) {
        onDispose {
            pressedInteraction.value?.let { oldValue ->
                val interaction = PressInteraction.Cancel(oldValue)
                interactionSource?.tryEmit(interaction)
                pressedInteraction.value = null
            }
        }
    }

    Modifier.pointerInput(interactionSource) {
        detectTapAndPress(
            onPress = {
                scope.launch {
                    // Remove any old interactions if we didn't fire stop / cancel properly
                    pressedInteraction.value?.let { oldValue ->
                        val interaction = PressInteraction.Cancel(oldValue)
                        interactionSource?.emit(interaction)
                        pressedInteraction.value = null
                    }
                    val interaction = PressInteraction.Press(it)
                    interactionSource?.emit(interaction)
                    pressedInteraction.value = interaction
                }
                val success = tryAwaitRelease()
                scope.launch {
                    pressedInteraction.value?.let { oldValue ->
                        val interaction =
                            if (success) {
                                PressInteraction.Release(oldValue)
                            } else {
                                PressInteraction.Cancel(oldValue)
                            }
                        interactionSource?.emit(interaction)
                        pressedInteraction.value = null
                    }
                }
            },
            onTap = { onTapState.value.invoke(it) }
        )
    }
} else this

internal fun Modifier.textFieldFocusModifier(
    enabled: Boolean,
    focusRequester: FocusRequester,
    interactionSource: MutableInteractionSource?,
    onFocusChanged: (FocusState) -> Unit
) = this
    .focusRequester(focusRequester)
    .onFocusChanged(onFocusChanged)
    .focusable(interactionSource = interactionSource, enabled = enabled)

fun Modifier.newSemantics(
    mergeDescendants: Boolean = false,
    properties: (SemanticsPropertyReceiver.() -> Unit)
): Modifier = this then SemanticsModifierCore(
    mergeDescendants = mergeDescendants,
    clearAndSetSemantics = false,
    properties = properties,
    inspectorInfo = debugInspectorInfo {
        name = "semantics"
        this.properties["mergeDescendants"] = mergeDescendants
        this.properties["properties"] = properties
    }
)