package com.deadrudolph.feature_player.ui.screen.player

import android.os.CountDownTimer
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_player.ui.model.SongContentLayoutResult
import com.deadrudolph.feature_player.utils.CountDownTimerFacade
import com.deadrudolph.feature_player_domain.domain.usecase.GetSongByIdUseCase
import com.deadrudolph.uicomponents.ui_model.SongState
import com.deadrudolph.uicomponents.utils.helper.SongUICompositionHelper
import com.deadrudolph.common_domain.model.Result
import com.deadrudolph.common_domain.model.Result.Loading
import com.deadrudolph.common_domain.model.Result.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal class PlayerScreenViewModelImpl @Inject constructor(
    private val getSongByIdUseCase: GetSongByIdUseCase
) : PlayerScreenViewModel() {

    private var songContentLayoutResult: SongContentLayoutResult? = null

    private var scrollValue = 0f

    private var countDownTimer: CountDownTimer? = null

    private var prevSetTime: Int = DEFAULT_SONG_TIME

    override val preCalculatedSongStateFlow: MutableStateFlow<Result<SongItem>> =
        MutableStateFlow(Loading(false))

    override val songPlayTimeState = MutableStateFlow(DEFAULT_SONG_TIME)

    override val playingStateFlow = MutableStateFlow(false)

    override val songState = MutableStateFlow(
        SongState()
    )
    override val chordDialogState: MutableStateFlow<ChordType?> = MutableStateFlow(null)

    override val timePickerStateFlow = MutableStateFlow(false)

    override fun fetchSongById(songId: String) {
        viewModelScope.launch {
            preCalculatedSongStateFlow.value = Loading(true)
            preCalculatedSongStateFlow.value = getSongByIdUseCase(songId)
        }
    }

    override fun onTextLayoutResult(result: TextLayoutResult) {
        val currentSong = (preCalculatedSongStateFlow.value as? Success)?.data ?: kotlin.run {
            Timber.e("Result must be success! Check your logic!")
            return
        }
        preCalculatedSongStateFlow.value = Loading(false)
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

    override fun onChordClicked(chord: ChordType) {
        chordDialogState.value = chord
    }

    override fun onChordDialogDismissed() {
        chordDialogState.value = null
    }

    override fun onPlayingStart(): Boolean {
        scrollValue = getScrollValuePerSec()
        if (scrollValue <= 0) {
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

    override fun onChordOffsetsChanged(offsets: List<IntOffset>, index: Int) {
        val currentState = songState.value
        songState.value = currentState.copy(
            textFields = currentState.textFields.toMutableList().apply {
                val item = getOrNull(index) ?: return
                set(index, item.copy(
                    chordsList = item.chordsList.mapIndexed { chIndex, chordUIModel ->
                        chordUIModel.copy(
                            horizontalOffset = chordUIModel.horizontalOffset + (offsets.getOrNull(
                                chIndex
                            )?.x ?: 0)
                        )
                    }
                ))
            }
        )
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
