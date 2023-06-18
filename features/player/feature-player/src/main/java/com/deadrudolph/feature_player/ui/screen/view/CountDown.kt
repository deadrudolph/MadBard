package com.deadrudolph.feature_player.ui.screen.view

import androidx.compose.foundation.clickable
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import java.util.Locale

@Composable
internal fun CountDown(
    modifier: Modifier,
    countDownStateValue: Int,
    onTimeClicked: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val locale = ConfigurationCompat.getLocales(configuration).get(0)

    Text(
        modifier = modifier
            .clickable {
                onTimeClicked()
            },
        text = secondsToTime(countDownStateValue, locale),
        style = CustomTheme.typography.countDown.copy(
            color = CustomTheme.colors.dark_600
        )
    )
}

private fun secondsToTime(secondsCount: Int, locale: Locale?): String {
    val minutes = secondsCount / 60
    val seconds = secondsCount % 60
    return String.format(
        locale,
        "%s:%s",
        toDisplayNumber(minutes),
        toDisplayNumber(seconds)
    )
}

private fun toDisplayNumber(value: Int): String {
    return when (value) {
        0 -> "00"
        in 1..9 -> String.format(
            "%s%d",
            "0",
            value
        )
        else -> value.toString()
    }
}