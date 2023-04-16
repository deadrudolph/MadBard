package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.ui.geometry.Offset

internal interface MouseSelectionObserver {
    // on start of shift click. if returns true event will be consumed
    fun onExtend(downPosition: Offset): Boolean
    // on drag after shift click. if returns true event will be consumed
    fun onExtendDrag(dragPosition: Offset): Boolean

    // if returns true event will be consumed
    fun onStart(downPosition: Offset, adjustment: NewSelectionAdjustment): Boolean
    fun onDrag(dragPosition: Offset, adjustment: NewSelectionAdjustment): Boolean
}