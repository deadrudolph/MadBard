package com.deadrudolph.uicomponents.view.textfield.core

import android.os.Build
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.TwoWayConverter
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.semantics.SemanticsPropertyKey
import androidx.compose.ui.unit.dp

internal val DefaultCursorThickness = 2.dp

internal const val SNAPSHOTS_INTERVAL_MILLIS = 5000

private const val Sqrt2 = 1.41421356f
internal val CursorHandleHeight = 25.dp
internal val CursorHandleWidth = CursorHandleHeight * 2f / (1 + Sqrt2)

internal val SelectionHandleInfoKey =
    SemanticsPropertyKey<SelectionHandleInfo>("SelectionHandleInfo")

internal val UnspecifiedSafeOffsetVectorConverter = TwoWayConverter<Offset, AnimationVector2D>(
    convertToVector = {
        if (it.isSpecified) {
            AnimationVector2D(it.x, it.y)
        } else {
            UnspecifiedAnimationVector2D
        }
    },
    convertFromVector = { Offset(it.v1, it.v2) }
)

internal val UnspecifiedAnimationVector2D = AnimationVector2D(Float.NaN, Float.NaN)

internal val OffsetDisplacementThreshold = Offset(
    Spring.DefaultDisplacementThreshold,
    Spring.DefaultDisplacementThreshold
)

internal val MagnifierSpringSpec = SpringSpec(visibilityThreshold = OffsetDisplacementThreshold)

internal val HandleWidth = 25.dp
internal val HandleHeight = 25.dp

internal const val isInTouchMode = true

internal val PointerEvent.isShiftPressed: Boolean
    get() = false
