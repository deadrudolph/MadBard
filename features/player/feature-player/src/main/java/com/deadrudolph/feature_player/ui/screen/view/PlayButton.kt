package com.deadrudolph.feature_player.ui.screen.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.deadrudolph.uicomponents.R.drawable
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun PlayButton(
    modifier: Modifier,
    isPlayingState: StateFlow<Boolean>,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier.then(
            Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false)
                ) {
                    onClick()
                }
        )
    ) {
        val isPlaying = isPlayingState.collectAsState().value

        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painterResource(
                id = if (isPlaying) drawable.ic_pause
                else drawable.ic_play
            ),
            contentDescription = null
        )
    }
}
