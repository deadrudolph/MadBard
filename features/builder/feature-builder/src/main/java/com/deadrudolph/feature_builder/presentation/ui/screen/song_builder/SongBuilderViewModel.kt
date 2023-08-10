package com.deadrudolph.feature_builder.presentation.ui.screen.song_builder

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.ViewModel
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.ui_model.ChordItemBlockModel
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.SongState
import com.puls.stateutil.Result
import kotlinx.coroutines.flow.StateFlow

internal abstract class SongBuilderViewModel : ViewModel() {

    abstract val chordBlockDeleteConfirmationDialogState: StateFlow<Int?>

    abstract val chordPickerStateFlow: StateFlow<Boolean>

    abstract val chordPickerForBlockStateFlow: StateFlow<Int?>

    abstract val chordEditDialogStateFlow: StateFlow<Pair<Int, ChordUIModel>?>

    abstract val textEditDialogStateFlow: StateFlow<Pair<Int, String>?>

    /**Fist - index of the block, Second - index of the chord inside the block*/
    abstract val chordEditDialogForBlockStateFlow: StateFlow<ChordItemBlockModel?>

    abstract val songPickerDialogStateFlow: StateFlow<Boolean>

    abstract val songListState: StateFlow<Result<List<SongItem>>>

    abstract val currentSongState: StateFlow<SongState>

    abstract val preCalculatedSongStateFlow: StateFlow<SongItem?>

    abstract fun onTextChanged(index: Int, textValue: TextFieldValue)

    abstract fun onLayoutResultChanged(textLayoutResult: TextLayoutResult, index: Int)

    abstract fun onNewChord(): Boolean

    abstract fun onChordSelected(chordType: ChordType)

    abstract fun onChordSelectionCancelled()

    abstract fun onTextFieldKeyBack(index: Int)

    abstract fun onCommonChordClicked(fieldIndex: Int, chord: ChordUIModel)

    abstract fun onChordsBlockChordClicked(chordItemBlockModel: ChordItemBlockModel)

    abstract fun onChordEditorDismissed()

    abstract fun onBlockChordEditorDismissed()

    abstract fun onTextEditorDismissed()

    abstract fun onChordRemoved(indexAndChord: Pair<Int, ChordUIModel>)

    abstract fun onChordRemovedFromBlock(blockIndex: Int, chordIndex: Int)

    abstract fun onSaveSongClicked()

    abstract fun onAddSongClicked()

    abstract fun onSongSelected(songItem: SongItem)

    abstract fun onCalculatedTextLayoutResult(result: TextLayoutResult)
    abstract fun onSongPickerDismissed()
    abstract fun onChordsBlockTextClicked(index: Int, text: String)

    abstract fun onChordBlockRemoved(index: Int)

    abstract fun onChordBlockAddChordClicked(index: Int)

    abstract fun onChordBlockChordSelected(index: Int, chordType: ChordType)

    abstract fun onChordsBlockTextChanged(index: Int, text: String)

    /**Returns true if the block can be added*/
    abstract fun onAddBlockClicked(title: String): Boolean

    abstract fun onChordBlockDeleteClicked(index: Int)

    abstract fun onConfirmationDismissed()

    abstract fun setSong(songItem: SongItem)

    abstract fun onChordOffsetsChanged(offsets: List<IntOffset>, index: Int)
}
