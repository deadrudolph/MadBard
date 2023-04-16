package com.deadrudolph.feature_builder.presentation.ui.text

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.feature_builder.presentation.ui.chord.ChordItem
import com.deadrudolph.feature_builder.presentation.ui.screen.SongBuilderViewModel
import com.deadrudolph.feature_builder.util.keyboard.keyboardHeightState
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
internal fun SongTextEditor(
    modifier: Modifier,
    homeViewModel: SongBuilderViewModel
) {

    val scrollableState = rememberScrollState()
    val keyboardHeightState = keyboardHeightState()
    val chords = homeViewModel.chordsListStateFlow.collectAsState()
    val textPaddingTop = 40.dp

    Box(
        modifier = modifier.then(
            Modifier.verticalScroll(scrollableState)
        )
    ) {
        val textValue = homeViewModel.textStateFlow.collectAsState()

        BasicTextField(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .defaultMinSize(minHeight = 100.dp)
                .padding(
                    top = textPaddingTop,
                    bottom = keyboardHeightState.value.keyboardHeight + 20.dp
                ),
            value = textValue.value,
            onValueChange = { value ->
                homeViewModel.onTextChanged(value)
            },
            cursorBrush = SolidColor(Color.White),
            textStyle = CustomTheme.typography.songsBuilder.copy(
                color = Color.White
            ),
            onTextLayout = { textLayoutResult ->
                
                homeViewModel.onLayoutResultChanged(textLayoutResult)
            }
        )

        chords.value.forEach { chord ->
            homeViewModel.getCoordsForPosition(chord.position)?.let { pointF ->
                ChordItem(
                    modifier = Modifier
                        .wrapContentSize(),
                    x = pointF.x.pxToDp.dp,
                    y = pointF.y.pxToDp.dp + textPaddingTop,
                    chord = chord
                )
            }
        }
    }
}