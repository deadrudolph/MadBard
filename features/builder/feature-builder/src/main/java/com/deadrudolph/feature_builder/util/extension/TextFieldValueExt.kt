package com.deadrudolph.feature_builder.util.extension

import androidx.compose.ui.text.input.TextFieldValue

fun TextFieldValue.getTrueSelectionStart(): Int =
    selection.start.coerceAtMost(selection.end)

fun TextFieldValue.getTrueSelectionEnd(): Int =
    selection.start.coerceAtLeast(selection.end)

fun TextFieldValue.getSelectionCenter(): Int {
    val trueStart = getTrueSelectionStart()
    val trueEnd = getTrueSelectionEnd()
    return (trueStart + ((trueEnd - trueStart) / 2)).coerceAtLeast(0)
}