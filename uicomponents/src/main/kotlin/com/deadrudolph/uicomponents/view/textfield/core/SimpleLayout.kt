package com.deadrudolph.uicomponents.view.textfield.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import com.deadrudolph.uicomponents.view.textfield.extension.fastFold
import com.deadrudolph.uicomponents.view.textfield.extension.fastForEach
import com.deadrudolph.uicomponents.view.textfield.extension.fastMap
import kotlin.math.max

@Composable
internal fun SimpleLayout(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Layout(modifier = modifier, content = content) { measurables, constraints ->
        val placeables = measurables.fastMap { measurable ->
            measurable.measure(constraints)
        }

        val width = placeables.fastFold(0) { maxWidth, placeable ->
            max(maxWidth, (placeable.width))
        }

        val height = placeables.fastFold(0) { minWidth, placeable ->
            max(minWidth, (placeable.height))
        }

        layout(width, height) {
            placeables.fastForEach { placeable ->
                placeable.place(0, 0)
            }
        }
    }
}