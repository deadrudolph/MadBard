package com.deadrudolph.feature_player.ui.screen.view

import android.widget.Toast
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.deadrudolph.feature_player.R.string
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun ControlsSection(
    modifier: Modifier,
    playingState: StateFlow<Boolean>,
    playingTimeStateValue: Int,
    onStopPlayClicked: () -> Unit,
    onPausePlayClicked: () -> Unit,
    onStartPlayClicked: () -> Boolean,
    onTimePickerClick: () -> Unit

) {
    Row(
        modifier = modifier
    ) {

        val context = LocalContext.current

        PlayButton(
            modifier = Modifier
                .size(80.dp, 80.dp)
                .padding(
                    start = 20.dp
                )
                .align(Alignment.CenterVertically),
            isPlayingState = playingState
        ) {
            if (playingState.value) onPausePlayClicked()
            else {
                val isSongStarted = onStartPlayClicked()
                if (!isSongStarted) Toast.makeText(
                    context,
                    string.player_song_too_short,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        StopButton(
            modifier = Modifier
                .size(60.dp, 60.dp)
                .padding(
                    start = 20.dp
                )
                .align(Alignment.CenterVertically)
        ) {
            onStopPlayClicked()
        }

        CountDown(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterVertically)
                .padding(start = 20.dp),
            countDownStateValue = playingTimeStateValue
        ) {
            onTimePickerClick()
        }
    }
}