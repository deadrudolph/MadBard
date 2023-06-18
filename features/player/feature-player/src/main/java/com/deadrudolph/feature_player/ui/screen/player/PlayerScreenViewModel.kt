package com.deadrudolph.feature_player.ui.screen.player

import androidx.compose.ui.text.TextLayoutResult
import androidx.lifecycle.ViewModel
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_player.ui.model.SongContentLayoutResult
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.SongState
import com.puls.stateutil.Result
import kotlinx.coroutines.flow.StateFlow

internal abstract class PlayerScreenViewModel : ViewModel() {

    abstract val songStateFlow: StateFlow<Result<SongItem>>

    abstract val songPlayTimeState: StateFlow<Int>

    abstract val playingStateFlow: StateFlow<Boolean>

    abstract val songState: StateFlow<SongState>

    abstract val chordDialogState: StateFlow<ChordUIModel?>

    abstract val timePickerStateFlow: StateFlow<Boolean>

    abstract fun fetchSongById(songId: String)

    abstract fun onTextLayoutResult(result: TextLayoutResult)

    abstract fun onChordClicked(chord: ChordUIModel)

    abstract fun onChordDialogDismissed()

    /**
     * true if player can be started. False if the song is too short
     * */
    abstract fun onPlayingStart(): Boolean

    abstract fun onPlayingStop()

    abstract fun onPlayingPause()

    abstract fun onTimePickerClick(isOpen: Boolean)

    abstract fun onSongDurationSet(seconds: Int)

    abstract fun getCurrentSongTimeSec(): Int

    abstract fun onContentGloballyPositioned(result: SongContentLayoutResult)

    abstract fun getScrollValue(): Float
}