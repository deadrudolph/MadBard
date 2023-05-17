package com.deadrudolph.feature_builder.presentation.ui.chord

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.deadrudolph.feature_builder.presentation.ui.model.ChordUIModel
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
internal fun ChordItem(
    modifier: Modifier,
    chord: ChordUIModel
) {

    Box(
        modifier = modifier.then(
            Modifier
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
        )
    ) {

        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 3.dp, vertical = 1.dp)
                .align(Alignment.Center),
            text = chord.chordType.marker,
            style = CustomTheme.typography.chord
        )
    }
}