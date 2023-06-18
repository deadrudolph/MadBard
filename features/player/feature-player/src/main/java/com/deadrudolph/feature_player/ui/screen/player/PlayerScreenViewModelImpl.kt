package com.deadrudolph.feature_player.ui.screen.player

import android.os.CountDownTimer
import android.widget.Toast
import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_player.ui.model.SongContentLayoutResult
import com.deadrudolph.feature_player.utils.CountDownTimerFacade
import com.deadrudolph.feature_player_domain.domain.usecase.GetSongByIdUseCase
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.SongState
import com.deadrudolph.uicomponents.utils.helper.SongUICompositionHelper
import com.deadrudolph.uicomponents.utils.logslogs
import com.puls.stateutil.Result
import com.puls.stateutil.Result.Loading
import com.puls.stateutil.Result.Success
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

internal class PlayerScreenViewModelImpl @Inject constructor(
    private val getSongByIdUseCase: GetSongByIdUseCase
) : PlayerScreenViewModel() {

    private var songContentLayoutResult: SongContentLayoutResult? = null

    private var scrollValue = 0f

    private var countDownTimer: CountDownTimer? = null

    private var prevSetTime: Int = DEFAULT_SONG_TIME

    override val songStateFlow: MutableStateFlow<Result<SongItem>> =
        MutableStateFlow(Loading(false))

    override val songPlayTimeState = MutableStateFlow(DEFAULT_SONG_TIME)

    override val playingStateFlow = MutableStateFlow(false)

    override val songState = MutableStateFlow(
        SongState()
    )
    override val chordDialogState: MutableStateFlow<ChordUIModel?> = MutableStateFlow(null)

    override val timePickerStateFlow = MutableStateFlow(false)

    override fun fetchSongById(songId: String) {
        viewModelScope.launch {
            songStateFlow.value = Loading(true)
            songStateFlow.value = getSongByIdUseCase(songId)
        }
    }

    override fun onTextLayoutResult(result: TextLayoutResult) {
        val currentSong = (songStateFlow.value as? Success)?.data ?: kotlin.run {
            Timber.e("Result must be success! Check your logic!")
            return
        }
        songStateFlow.value = Loading(false)
        val textFields = SongUICompositionHelper.songItemToTextFieldsStateList(
            textLayoutResult = result,
            songItem = currentSong
        )
        songState.value = SongState(
            title = currentSong.title,
            textFields = textFields,
            imagePath = currentSong.imagePath
        )
    }

    override fun onChordClicked(chord: ChordUIModel) {
        chordDialogState.value = chord
    }

    override fun onChordDialogDismissed() {
        chordDialogState.value = null
    }

    override fun onPlayingStart(): Boolean {
        scrollValue = getScrollValuePerSec()
        if(scrollValue <= 0) {
            return false
        }
        playingStateFlow.value = true
        countDownTimer = CountDownTimerFacade(
            millisInFuture = songPlayTimeState.value * 1000L,
            onValueChanged = { millis ->
                songPlayTimeState.value = millis.toInt() / 1000
            },
            onFinished = {
                playingStateFlow.value = false
                songPlayTimeState.value = prevSetTime
                scrollValue = 0f
            }
        )
        countDownTimer?.start()
        return true
    }

    override fun onPlayingPause() {
        playingStateFlow.value = false
        countDownTimer?.cancel()
        countDownTimer = null
    }

    override fun onPlayingStop() {
        playingStateFlow.value = false
        songPlayTimeState.value = prevSetTime
        scrollValue = 0f
        countDownTimer?.cancel()
        countDownTimer = null
    }

    override fun onTimePickerClick(isOpen: Boolean) {
        onPlayingPause()
        timePickerStateFlow.value = isOpen
    }

    override fun onSongDurationSet(seconds: Int) {
        prevSetTime = seconds
        songPlayTimeState.value = seconds
    }

    override fun getCurrentSongTimeSec(): Int {
        return songPlayTimeState.value
    }

    override fun onContentGloballyPositioned(result: SongContentLayoutResult) {
        songContentLayoutResult = result
    }

    override fun getScrollValue(): Float {
        return scrollValue
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
        countDownTimer = null
    }

    private fun getScrollValuePerSec(): Float {
        val layoutResult = songContentLayoutResult ?: return 0f
        val scrollHeight = with(layoutResult) {
            contentHeight - (screenHeight - contentPositionY)
        }
        return scrollHeight.toFloat() / songPlayTimeState.value
    }

    private companion object {
        private const val DEFAULT_SONG_TIME = 150
    }
}