package com.deadrudolph.feature_builder.util.extension

import androidx.compose.ui.unit.IntOffset

fun IntOffset.isZero(): Boolean {
    return x == 0 && y == 0
}
