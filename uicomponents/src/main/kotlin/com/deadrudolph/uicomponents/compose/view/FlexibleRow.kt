package com.deadrudolph.uicomponents.compose.view

import androidx.annotation.FloatRange
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy

@Composable
fun FlexibleRow(
    modifier: Modifier = Modifier,
    @FloatRange(from = 0.1, to = 1.0) overlapFactor: Float = 0.5f,
    content: @Composable () -> Unit,
) {

    val measurePolicy = MeasurePolicy { measurables, constraints ->
        val placeables = measurables.map { measurable ->
            measurable.measure(constraints)
        }
        val height = placeables.maxOf { it.height }
        val width = (placeables.subList(1, placeables.size)
            .sumOf { it.width } * overlapFactor + placeables[0].width).toInt()
        layout(width, height) {
            var xPos = 0
            for (placeable in placeables) {
                placeable.place(xPos, 0, 0f)
                xPos += (placeable.width * overlapFactor).toInt()
            }
        }
    }

    Layout(
        measurePolicy = measurePolicy,
        content = content,
        modifier = modifier
    )
}
