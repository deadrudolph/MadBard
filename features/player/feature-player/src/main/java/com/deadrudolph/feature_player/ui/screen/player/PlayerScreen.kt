package com.deadrudolph.feature_player.ui.screen.player

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.androidx.AndroidScreen
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.core.base.action.ActivityActions
import com.deadrudolph.feature_player.di.component.PlayerComponentHolder
import com.deadrudolph.feature_player.ui.dialog.ChordViewDialog
import com.deadrudolph.feature_player.ui.screen.view.SongContent
import com.deadrudolph.feature_player.ui.screen.view.TextFieldForCalculation
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme
import com.deadrudolph.uicomponents.compose.view.SongDurationPicker
import com.deadrudolph.uicomponents.utils.LoadState
import com.deadrudolph.uicomponents.utils.logslogs

internal class PlayerScreen(
    private val songItemId: String
) : AndroidScreen() {

    @Composable
    override fun Content() {

        DefaultTheme {
            Screen()
        }
    }

    @Composable
    private fun Screen() {

        (LocalContext.current as? ActivityActions)?.onBottomBarVisible(false)

        val playerViewModel =
            getDaggerViewModel<PlayerScreenViewModel>(
                viewModelProviderFactory = PlayerComponentHolder.getInternal().getViewModelFactory()
            )

        LaunchedEffect(key1 = Unit) {
            playerViewModel.fetchSongById(songItemId)
        }

        ScreenContent(playerViewModel)
    }

    @Composable
    fun ScreenContent(playerViewModel: PlayerScreenViewModel) {

        playerViewModel
            .songStateFlow
            .collectAsState()
            .value
            .LoadState(
                onRestartState = {}
            ) { songItem ->
                TextFieldForCalculation(
                    songItem = songItem,
                    onTextLayoutResult = playerViewModel::onTextLayoutResult
                )
            }

        SongContent(
            songState = playerViewModel.songState,
            playingState = playerViewModel.playingStateFlow,
            onChordClicked = playerViewModel::onChordClicked,
            playingTimeState = playerViewModel.songPlayTimeState,
            getScrollingStateValue = playerViewModel::getScrollValue,
            onStartPlayClicked = playerViewModel::onPlayingStart,
            onStopPlayClicked = playerViewModel::onPlayingStop,
            onPausePlayClicked = playerViewModel::onPlayingPause,
            onTimePickerClick = {
                playerViewModel.onTimePickerClick(true)
            },
            onSongGloballyPositioned = playerViewModel::onContentGloballyPositioned
        )

        playerViewModel
            .chordDialogState
            .collectAsState()
            .value
            ?.let {
                ChordViewDialog(
                    chord = it,
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(top = 50.dp),
                    onDismiss = playerViewModel::onChordDialogDismissed
                )
            }

        val isShowTimePicker = playerViewModel
            .timePickerStateFlow
            .collectAsState()
            .value

        if (isShowTimePicker) {

            Dialog(
                onDismissRequest = {
                    playerViewModel.onTimePickerClick(false)
                }
            ) {
                SongDurationPicker(
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp),
                    initialTimeSec = playerViewModel.getCurrentSongTimeSec(),
                    minutesMax = 9,
                    secondsMax = 59,
                    onValueChanged = playerViewModel::onSongDurationSet
                )
            }
        }
    }
}