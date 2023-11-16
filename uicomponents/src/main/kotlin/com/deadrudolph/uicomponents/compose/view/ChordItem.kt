package com.deadrudolph.uicomponents.compose.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
fun ChordItem(
    modifier: Modifier,
    chordName: String
) {
    Box(
        modifier = modifier
    ) {

        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 3.dp, vertical = 1.dp)
                .align(Alignment.Center),
            text = chordName,
            style = CustomTheme.typography.chord,
            maxLines = 1
        )
    }
}
