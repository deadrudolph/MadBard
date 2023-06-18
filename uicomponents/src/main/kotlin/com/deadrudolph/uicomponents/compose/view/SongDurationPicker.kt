package com.deadrudolph.uicomponents.compose.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.chargemap.compose.numberpicker.NumberPicker
import com.deadrudolph.uicomponents.R
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
fun SongDurationPicker(
    modifier: Modifier,
    initialTimeSec: Int,
    minutesMax: Int,
    secondsMax: Int,
    onValueChanged: (Int) -> Unit,
) {

    val minutesValueState = remember {
        mutableStateOf(initialTimeSec / 60)
    }

    val secondsValueState = remember {
        mutableStateOf(initialTimeSec % 60)
    }

    Row(modifier = modifier) {
        NumberPicker(
            modifier = Modifier
                .background(Color.Transparent)
                .weight(1f)
                .fillMaxHeight(),
            value = minutesValueState.value,
            onValueChange = { value ->
                minutesValueState.value = value
                onValueChanged((value * 60) + secondsValueState.value)
            },
            range = 0..minutesMax,
            textStyle = CustomTheme.typography.title.copy(
                color = Color.White
            ),
            dividersColor = Color.Transparent
        )

        Text(
            modifier = Modifier
                .wrapContentSize()
                .align(CenterVertically),
            text = stringResource(id = R.string.common_unit_min),
            style = CustomTheme.typography.title.copy(
                color = Color.White
            )
        )

        NumberPicker(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(Color.Transparent),
            value = secondsValueState.value,
            onValueChange = { value ->
                secondsValueState.value = value
                onValueChanged((minutesValueState.value * 60) + value)
            },
            range = 0..secondsMax,
            textStyle = CustomTheme.typography.title.copy(
                color = Color.White
            ),
            dividersColor = Color.Transparent
        )

        Text(
            modifier = Modifier
                .wrapContentSize()
                .align(CenterVertically),
            text = stringResource(id = R.string.common_unit_sec),
            style = CustomTheme.typography.title.copy(
                color = Color.White
            )
        )
    }
}