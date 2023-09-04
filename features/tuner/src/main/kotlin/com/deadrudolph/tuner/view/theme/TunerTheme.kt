package com.deadrudolph.tuner.view.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun ChromaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = TunerColors(),
        typography = TunerTypography(),
        content = content
    )
}
