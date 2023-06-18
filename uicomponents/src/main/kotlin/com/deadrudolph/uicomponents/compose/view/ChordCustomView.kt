package com.deadrudolph.uicomponents.compose.view

import android.graphics.Typeface
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_domain.model.ChordType

@Composable
fun ChordCustomView(
    modifier: Modifier,
    chordType: ChordType,
    onChordClicked: ((ChordType) -> Unit)? = null
) {
    Box(
        modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .apply {
                    onChordClicked?.let {
                        clickable {
                            onChordClicked(chordType)
                        }
                    }
                }
        )
    ) {
        ChordView(
            modifier = Modifier.fillMaxSize(),
            chordType = chordType
        )
    }
}

@Composable
private fun ChordView(
    modifier: Modifier,
    chordType: ChordType
) {
    Canvas(
        modifier = modifier
    ) {
        val verticalOffset = size.height * 0.05f
        val endOffset = size.width * 0.2f
        val startOffset = size.width * 0.07f

        val rectSizeWithOffset = Size(
            size.width - endOffset - startOffset,
            size.height - verticalOffset * 2
        )

        drawEdgeLines(
            verticalOffset = verticalOffset,
            startOffset = startOffset,
            endOffset = endOffset
        )

        val minGuitarFretsCount = 3
        val firstGuitarFret = chordType.scheme.filter { it != 0 }.min()
        val actualChordsCount = chordType.scheme.max() - firstGuitarFret
        val guitarFretsCount = minGuitarFretsCount.coerceAtLeast(
            actualChordsCount
        )
        val lastGuitarFret = firstGuitarFret + guitarFretsCount

        val guitarFretsOffset = rectSizeWithOffset.height / guitarFretsCount.inc()
        val guitarStringsOffset = rectSizeWithOffset.width / chordType.scheme.size.dec()

        drawGuitarFrets(
            verticalOffset = verticalOffset,
            startOffset = startOffset,
            endOffset = endOffset,
            rectSizeWithOffset = rectSizeWithOffset,
            firstGuitarFret = firstGuitarFret,
            lastGuitarFret = lastGuitarFret,
            guitarFretsOffset = guitarFretsOffset
        )

        drawGuitarStrings(
            chordType = chordType,
            guitarStringsOffset = guitarStringsOffset,
            verticalOffset = verticalOffset,
            startOffset = startOffset
        )

        drawFingerPositions(
            chordType = chordType,
            verticalOffset = verticalOffset,
            startOffset = startOffset,
            guitarFretsOffset = guitarFretsOffset,
            guitarStringsOffset = guitarStringsOffset,
            firstGuitarFret = firstGuitarFret
        )
    }
}

private fun DrawScope.drawEdgeLines(
    verticalOffset: Float,
    startOffset: Float,
    endOffset: Float
) {
    drawLine(
        brush = SolidColor(Color.Black),
        start = Offset(
            startOffset,
            verticalOffset
        ),
        end = Offset(
            size.width - endOffset,
            verticalOffset
        ),
        strokeWidth = 10f
    )

    drawLine(
        brush = SolidColor(Color.Black),
        start = Offset(
            startOffset,
            size.height - verticalOffset
        ),
        end = Offset(
            size.width - endOffset,
            size.height - verticalOffset
        ),
        strokeWidth = 3f
    )
}

private fun DrawScope.drawFingerPositions(
    chordType: ChordType,
    verticalOffset: Float,
    startOffset: Float,
    guitarFretsOffset: Float,
    guitarStringsOffset: Float,
    firstGuitarFret: Int
) {

    val pointRadius = guitarFretsOffset.coerceAtMost(
        guitarStringsOffset
    ) * 0.25f

    chordType.scheme.reversed().forEachIndexed { index, value ->
        if (value == 0) return@forEachIndexed
        val realPosition = value - firstGuitarFret
        drawCircle(
            brush = SolidColor(Color.Red),
            radius = pointRadius,
            center = Offset(
                x = (index * guitarStringsOffset) + startOffset,
                y = (realPosition * guitarFretsOffset) + verticalOffset + guitarFretsOffset / 2
            )
        )
    }

}

private fun DrawScope.drawGuitarFrets(
    verticalOffset: Float,
    startOffset: Float,
    endOffset: Float,
    rectSizeWithOffset: Size,
    firstGuitarFret: Int,
    lastGuitarFret: Int,
    guitarFretsOffset: Float
) {

    val dotsPositions = listOf(3, 5, 7, 9, 15, 17, 19, 21)
    val doubleDotsPositions = listOf(12, 24)

    (firstGuitarFret..lastGuitarFret).forEachIndexed { index, value ->

        val dotRadius = guitarFretsOffset * 0.3f

        val paint = Paint().apply {
            color = Color.Black
        }.asFrameworkPaint().apply {
            typeface = Typeface.DEFAULT_BOLD
            textSize = guitarFretsOffset * 0.35f
        }

        drawIntoCanvas {
            it.nativeCanvas.drawText(
                value.toString(),
                size.width - endOffset + dotRadius * 0.55f,
                verticalOffset + guitarFretsOffset * 0.6f +
                        (guitarFretsOffset * index),
                paint
            )
        }

        drawLine(
            brush = SolidColor(Color.DarkGray),
            start = Offset(
                startOffset,
                verticalOffset + guitarFretsOffset +
                        (guitarFretsOffset * index)

            ),
            end = Offset(
                size.width - endOffset,
                verticalOffset + guitarFretsOffset +
                        (guitarFretsOffset * index)
            ),
            strokeWidth = 2f
        )

        val circleColor = Color(0xFFC7C7C7)

        if (dotsPositions.contains(value)) {
            drawCircle(
                brush = SolidColor(circleColor),
                radius = dotRadius,
                center = Offset(
                    (rectSizeWithOffset.width / 2f) + startOffset,
                    verticalOffset + guitarFretsOffset / 2f +
                            (guitarFretsOffset * index)
                )
            )
        }

        if (doubleDotsPositions.contains(value)) {

            drawCircle(
                brush = SolidColor(circleColor),
                radius = dotRadius,
                center = Offset(
                    (rectSizeWithOffset.width / 4f) + startOffset,
                    verticalOffset + guitarFretsOffset / 2f +
                            (guitarFretsOffset * index)
                )
            )

            drawCircle(
                brush = SolidColor(circleColor),
                radius = dotRadius,
                center = Offset(
                    (rectSizeWithOffset.width / 4f * 3f) + startOffset,
                    verticalOffset + guitarFretsOffset / 2f +
                            (guitarFretsOffset * index)
                )
            )
        }
    }

}

private fun DrawScope.drawGuitarStrings(
    chordType: ChordType,
    guitarStringsOffset: Float,
    verticalOffset: Float,
    startOffset: Float
) {

    repeat(chordType.scheme.size) { index ->
        drawLine(
            brush = SolidColor(Color.Black),
            start = Offset(
                startOffset +
                        (guitarStringsOffset * index),
                verticalOffset
            ),
            end = Offset(
                startOffset +
                        (guitarStringsOffset * index),
                size.height - verticalOffset
            ),
            strokeWidth = 2f
        )
    }
}