package com.deadrudolph.uicomponents.utils.composition_locals

import android.util.Size
import androidx.compose.runtime.compositionLocalOf

val LocalContentSize = compositionLocalOf {
    Size(0, 0)
}