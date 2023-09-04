package com.deadrudolph.uicomponents.compose.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.common_utils.extension.setOrAdd
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import kotlinx.collections.immutable.ImmutableList

@Composable
fun NotOverlappingBox(
    modifier: Modifier = Modifier,
    chordsList: ImmutableList<ChordUIModel>,
    onChordClicked: (ChordUIModel) -> Unit,
    onChordOffsetsChanged: (List<IntOffset>) -> Unit
) {
    if (chordsList.isEmpty()) return
    val state = rememberScrollState()
    Box(
        modifier = modifier
            .horizontalScroll(state)
            .background(
                if (state.value <= 3) Color.Transparent
                else CustomTheme.colors.warning_red
            )
    ) {
        val layoutResultList =
            mutableListOf<LayoutCoordinates?>(*chordsList.map { null }.toTypedArray())

        val chordSizeList = remember {
            mutableListOf<IntSize?>(*chordsList.map { null }.toTypedArray())
        }

        chordsList.forEachIndexed { index, chord ->
            ChordItem(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(
                        start = chord.horizontalOffset.pxToDp.dp,
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .clickable {
                        onChordClicked(chord)
                    }
                    .onPlaced { layoutCoordinates ->
                        layoutResultList.setOrAdd(index, layoutCoordinates)
                        chordSizeList.setOrAdd(index, layoutCoordinates.size)
                        if (layoutResultList.indices.last == index && layoutResultList.all {
                                it != null
                            }) {
                            calculateOffsets(
                                layoutResultList,
                                onChordOffsetsChanged
                            )
                            layoutResultList.clear()
                            layoutResultList.addAll(chordsList.map { null })
                        }
                    },
                chordName = chord.chordType.marker
            )
        }
    }
}

private fun calculateOffsets(
    layoutResultList: MutableList<LayoutCoordinates?>,
    onChordOffsetsChanged: (List<IntOffset>) -> Unit,
): List<IntOffset> {
    val offsets = mutableListOf(*layoutResultList.map {
        IntOffset(0, 0)
    }.toTypedArray())

    layoutResultList.forEachIndexed { index, layoutCoordinates ->
        layoutCoordinates ?: return@forEachIndexed
        val offset = offsets.getOrNull(index) ?: return@forEachIndexed
        val prevCoords = layoutResultList.getOrNull(index.dec()) ?: return@forEachIndexed

        if (isItemsOrderWrongOrOverlap(
                currentOffset = layoutCoordinates.positionInParent(),
                currentSize = layoutCoordinates.size,
                prevOffset = prevCoords.positionInParent(),
                prevSize = prevCoords.size
            )
        ) {
            val currentX = layoutCoordinates.positionInParent().x.toInt()
            offsets[index] = offset + IntOffset(
                x = (prevCoords.positionInParent().x + prevCoords.size.width)
                    .toInt()
                    .inc() - currentX,
                y = 0
            )
        }
    }

    onChordOffsetsChanged(offsets)

    return offsets
}

private fun isOverlap(
    firstSize: IntSize,
    firstOffset: Offset,
    secondSize: IntSize,
    secondOffset: Offset
): Boolean {
    val firstXRange = firstOffset.x.toInt()..(firstOffset.x.toInt() + firstSize.width)
    val secondXRange = secondOffset.x.toInt()..(secondOffset.x.toInt() + secondSize.width)
    val firstYRange = firstOffset.y.toInt()..(firstOffset.y.toInt() + firstSize.height)
    val secondYRange = secondOffset.y.toInt()..(secondOffset.y.toInt() + secondSize.height)
    return firstXRange.intersect(secondXRange) && firstYRange.intersect(secondYRange)
}

private fun isItemsOrderWrongOrOverlap(
    prevSize: IntSize,
    prevOffset: Offset,
    currentSize: IntSize,
    currentOffset: Offset
): Boolean {
    val isOverlap = isOverlap(
        firstSize = prevSize,
        firstOffset = prevOffset,
        secondSize = currentSize,
        secondOffset = currentOffset
    )
    return isOverlap || prevOffset.x + prevSize.width > currentOffset.x
}

private fun IntRange.intersect(other: IntRange): Boolean {
    return first in other || last in other || other.first in this || other.last in this
}



