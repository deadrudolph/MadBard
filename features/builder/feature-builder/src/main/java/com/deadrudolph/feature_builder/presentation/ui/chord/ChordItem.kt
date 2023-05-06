package com.deadrudolph.feature_builder.presentation.ui.chord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.home_domain.domain.model.songs_dashboard.Chord
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@OptIn(ExperimentalTextApi::class)
@Composable
fun ChordItem(
    modifier: Modifier,
    x: Dp,
    y: Dp,
    chord: Chord,
    onChordClicked: ((Chord) -> Unit)? = null
) {
    val textMeasurer = rememberTextMeasurer()
    val textStyle = CustomTheme.typography.chord
    val textWidth = textMeasurer.measure(
        AnnotatedString(chord.chordType.marker),
        style = textStyle
    ).size.width.pxToDp
    Box(
        modifier = modifier.then(
            Modifier
                .offset(
                    x = x - (textWidth / 2).dp,
                    y = y
                )
                .clip(RoundedCornerShape(10.dp))
                .background(Color.White)
                .clickable {
                    onChordClicked?.invoke(chord)
                }
        )
    ) {

        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 5.dp, vertical = 2.dp)
                .align(Alignment.Center),
            text = chord.chordType.marker,
            style = textStyle
        )
    }
}