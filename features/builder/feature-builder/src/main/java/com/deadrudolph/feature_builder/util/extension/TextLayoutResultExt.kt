package com.deadrudolph.feature_builder.util.extension

import androidx.compose.ui.text.TextLayoutResult
import timber.log.Timber

fun TextLayoutResult.getSelectedLineStartIndex(
    selectionCenterIndex: Int
): Int {
    val currentLine = getLineForOffset(
        selectionCenterIndex.inc()
    )
    return try {
        getLineStart(currentLine)
    } catch (e: IllegalArgumentException) {
        Timber.e("Could not get line start for line: $currentLine")
        0
    }
}