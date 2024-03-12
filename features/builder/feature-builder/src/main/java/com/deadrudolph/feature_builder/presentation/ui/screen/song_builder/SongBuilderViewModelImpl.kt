package com.deadrudolph.feature_builder.presentation.ui.screen.song_builder

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.IntOffset
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.ui_model.ChordItemBlockModel
import com.deadrudolph.feature_builder.util.extension.isZero
import com.deadrudolph.feature_builder.util.extension.removeChord
import com.deadrudolph.feature_builder.util.manager.SongBuilderTextFieldsManager
import com.deadrudolph.feature_builder.util.mapper.TextFieldStateListToCalculatedSongMapper
import com.deadrudolph.feature_builder_domain.domain.usecase.SaveSongUseCase
import com.deadrudolph.home_domain.domain.usecase.chords.GetAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.SongState
import com.deadrudolph.common_domain.model.Result
import com.deadrudolph.common_domain.model.Result.Loading
import com.deadrudolph.common_domain.model.Result.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal class SongBuilderViewModelImpl @Inject constructor(
    private val textFieldStateListToCalculatedSongMapper: TextFieldStateListToCalculatedSongMapper,
    private val saveSongUseCase: SaveSongUseCase,
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val songBuilderTextFieldsManager: SongBuilderTextFieldsManager,
    private val getAllChordsUseCase: GetAllChordsUseCase
) : SongBuilderViewModel() {

    override val chordBlockDeleteConfirmationDialogState = MutableStateFlow<Int?>(null)
    override val chordPickerStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val chordPickerForBlockStateFlow = MutableStateFlow<Int?>(null)
    override val chordEditDialogStateFlow = MutableStateFlow<Pair<Int, ChordUIModel>?>(null)
    override val textEditDialogStateFlow = MutableStateFlow<Pair<Int, String>?>(null)
    override val chordEditDialogForBlockStateFlow = MutableStateFlow<ChordItemBlockModel?>(
        null
    )
    override val songPickerDialogStateFlow = MutableStateFlow(false)
    override val songListState = MutableStateFlow<Result<List<SongItem>>>(
        Loading(false)
    )
    override val currentSongState: StateFlow<SongState> =
        songBuilderTextFieldsManager.currentSongState
    override val preCalculatedSongStateFlow = MutableStateFlow<SongItem?>(null)
    override val allChordsStateFlow = MutableStateFlow<Result<List<ChordType>>>(Loading(false))

    private var calculatedTextLayoutResult: TextLayoutResult? = null

    override fun onTextChanged(index: Int, textValue: TextFieldValue) {
        songBuilderTextFieldsManager.setFocus(index)
        songBuilderTextFieldsManager.setFocusAndChords(index, textValue)
        songBuilderTextFieldsManager.mergeTextFieldWithPreviousIfNeeded()
    }

    override fun onNewChord(): Boolean {
        return if (songBuilderTextFieldsManager
            .getSelectedTextFieldState()
            ?.value
            ?.text
            .isNullOrEmpty()
        ) false
        else {
            chordPickerStateFlow.value = true
            true
        }
    }

    override fun setSong(songItem: SongItem) {
        preCalculatedSongStateFlow.value = songItem
    }

    override fun onChordOffsetsChanged(offsets: List<IntOffset>, index: Int) {
        songBuilderTextFieldsManager.setTextFields { textFieldStates ->
            textFieldStates.mapIndexed { stateIndex, textFieldState ->
                if (index == stateIndex) {
                    textFieldState.copy(
                        chordsList = textFieldState.chordsList.mapIndexed { chIndex, chordUIModel ->
                            offsets.getOrNull(chIndex)?.let { offset ->
                                if (offset.isZero()) chordUIModel
                                else {
                                    val position =
                                        songBuilderTextFieldsManager.getPositionForOffset(
                                            index, Offset(
                                                x = chordUIModel.horizontalOffset + offset.x.toFloat(),
                                                y = offset.y.toFloat()
                                            )
                                        ) ?: chordUIModel.position
                                    chordUIModel.copy(
                                        position = position,
                                        horizontalOffset = chordUIModel.horizontalOffset + offset.x,
                                    )
                                }
                            } ?: chordUIModel
                        }.sortedBy { it.horizontalOffset }
                    )
                } else textFieldState
            }
        }
    }

    override fun fetchChordsIfNotFetched() {
        if ((allChordsStateFlow.value as? Success)?.data.isNullOrEmpty().not()) return
        viewModelScope.launch {
            allChordsStateFlow.value = Loading(true)
            allChordsStateFlow.value = getAllChordsUseCase()
        }
    }

    override fun onChordSelected(chordType: ChordType) {
        chordPickerStateFlow.value = false
        songBuilderTextFieldsManager.onChordSelected(chordType)
    }

    override fun onChordSelectionCancelled() {
        chordPickerStateFlow.value = false
        chordPickerForBlockStateFlow.value = null
    }

    override fun onTextFieldKeyBack(index: Int) {
        songBuilderTextFieldsManager.tryRemoveTextField(index)
    }

    override fun onCommonChordClicked(fieldIndex: Int, chord: ChordUIModel) {
        chordEditDialogStateFlow.value = fieldIndex to chord
    }

    override fun onChordsBlockChordClicked(chordItemBlockModel: ChordItemBlockModel) {
        chordEditDialogForBlockStateFlow.value = chordItemBlockModel
    }

    override fun onChordsBlockTextClicked(index: Int, text: String) {
        textEditDialogStateFlow.value = index to text
    }

    override fun onChordBlockRemoved(index: Int) {
        chordBlockDeleteConfirmationDialogState.value = null
        songBuilderTextFieldsManager.setTextFields { textFieldStates ->
            textFieldStates.mapIndexed { i, value ->
                if (index == i) value.copy(
                    chordBlock = null
                ) else value
            }
        }
    }

    override fun onChordBlockDeleteClicked(index: Int) {
        chordBlockDeleteConfirmationDialogState.value = index
    }

    override fun onConfirmationDismissed() {
        chordBlockDeleteConfirmationDialogState.value = null
    }

    override fun onChordBlockAddChordClicked(index: Int) {
        chordPickerForBlockStateFlow.value = index
    }

    override fun onChordBlockChordSelected(index: Int, chordType: ChordType) {
        chordPickerForBlockStateFlow.value = null
        songBuilderTextFieldsManager.setTextFields { textFieldStates ->
            textFieldStates.mapIndexed { i, state ->
                if (index == i) state.copy(
                    chordBlock = state.chordBlock?.copy(
                        chordsList = state.chordBlock?.chordsList?.toMutableList()?.apply {
                            add(chordType)
                        }.orEmpty()
                    )
                ) else state
            }
        }
    }

    override fun onChordsBlockTextChanged(index: Int, text: String) {
        textEditDialogStateFlow.value = null
        songBuilderTextFieldsManager.setTextFields { textFields ->
            textFields.mapIndexed { i, textFieldState ->
                if (i == index) {
                    val currentBlock = textFieldState.chordBlock
                    textFieldState.copy(
                        chordBlock = if (text.isBlank() &&
                            currentBlock?.chordsList.isNullOrEmpty()
                        ) null else currentBlock?.copy(title = text)
                    )
                } else textFieldState
            }
        }
    }

    override fun onChordEditorDismissed() {
        chordEditDialogStateFlow.value = null
    }

    override fun onBlockChordEditorDismissed() {
        chordEditDialogForBlockStateFlow.value = null
    }

    override fun onTextEditorDismissed() {
        textEditDialogStateFlow.value = null
    }

    override fun onChordRemoved(indexAndChord: Pair<Int, ChordUIModel>) {
        chordEditDialogStateFlow.value = null
        songBuilderTextFieldsManager.setTextFields { textFields ->
            textFields.mapIndexed { index, textFieldState ->
                if (index == indexAndChord.first) textFieldState.removeChord(indexAndChord.second)
                else textFieldState
            }
        }
        songBuilderTextFieldsManager.mergeTextFieldWithPreviousIfNeeded(indexAndChord.first)
    }

    override fun onChordRemovedFromBlock(blockIndex: Int, chordIndex: Int) {
        chordEditDialogForBlockStateFlow.value = null
        songBuilderTextFieldsManager.setTextFields { textFields ->
            textFields.mapIndexed { index, textFieldState ->
                if (index == blockIndex) {
                    val block = textFieldState.chordBlock
                    var newBlock = block?.copy(
                        chordsList = block.chordsList.toMutableList().apply {
                            removeAt(chordIndex)
                        }
                    )
                    if (newBlock?.chordsList.isNullOrEmpty() && newBlock?.title.isNullOrBlank()) {
                        newBlock = null
                    }
                    textFieldState.copy(
                        chordBlock = newBlock
                    )
                } else textFieldState
            }
        }
    }

    override fun onSaveSongClicked() {
        val result = textFieldStateListToCalculatedSongMapper(
            songBuilderTextFieldsManager.currentFields.mapIndexed { index, textField ->
                textField.copy(
                    chordsList = songBuilderTextFieldsManager.adjustOffsets(
                        textField.chordsList,
                        index
                    )
                )
            }
        )
        if (result.songText.isBlank()) return
        viewModelScope.launch {
            saveSongUseCase.invoke(
                SongItem(
                    id = System.currentTimeMillis().toString(),
                    createTimeMillis = System.currentTimeMillis(),
                    title = result.songText.take(10),
                    imagePath = "",
                    text = result.songText,
                    chords = result.chords,
                    chordBlocks = result.chordBlocks
                )
            )
        }
    }

    override fun onAddSongClicked() {
        songPickerDialogStateFlow.value = true
        songListState.value = Loading(true)
        viewModelScope.launch {
            songListState.value = getAllSongsUseCase()
        }
    }

    override fun onSongSelected(songItem: SongItem) {
        songPickerDialogStateFlow.value = false
        preCalculatedSongStateFlow.value = songItem
    }

    override fun onCalculatedTextLayoutResult(result: TextLayoutResult) {
        calculatedTextLayoutResult = result

        val currentSong = preCalculatedSongStateFlow.value ?: kotlin.run {
            Timber.e("Song must not be null! Check your logic!")
            return
        }
        songBuilderTextFieldsManager.calculateSong(currentSong, result)
    }

    override fun onAddBlockClicked(title: String): Boolean {
        return songBuilderTextFieldsManager.insertBlock(title)
    }

    override fun onSongPickerDismissed() {
        songPickerDialogStateFlow.value = false
    }

    override fun onLayoutResultChanged(textLayoutResult: TextLayoutResult, index: Int) {
        songBuilderTextFieldsManager.applyToLayoutResult {
            while (index > size.dec()) {
                add(null)
            }
            set(index, textLayoutResult)
        }
    }

    override fun getChordsOrEmpty(): List<ChordType> {
        return (allChordsStateFlow.value as? Success)?.data.orEmpty()
    }
}
