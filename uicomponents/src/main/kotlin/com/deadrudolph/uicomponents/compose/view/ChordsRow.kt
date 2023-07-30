package com.deadrudolph.uicomponents.compose.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.uicomponents.ui_model.ChordUIModel

@Composable
fun ChordsRow(
    modifier: Modifier,
    chordsList: List<ChordUIModel>,
    onChordClicked: (ChordUIModel) -> Unit
) {
    if (chordsList.isEmpty()) return
    Box(
        modifier = modifier
    ) {
        chordsList.forEach { chord ->
            ChordItem(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = chord.horizontalOffset.pxToDp.dp)
                    .clickable {
                        onChordClicked(chord)
                    },
                chordName = chord.chordType.marker
            )
        }
    }
}
