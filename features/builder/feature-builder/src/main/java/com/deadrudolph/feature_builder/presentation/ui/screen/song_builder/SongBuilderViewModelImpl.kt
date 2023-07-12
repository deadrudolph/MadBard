package com.deadrudolph.feature_builder.presentation.ui.screen.song_builder

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.ChordBlock
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.ui_model.ChordItemBlockModel
import com.deadrudolph.feature_builder.util.extension.getSelectedLineStartIndex
import com.deadrudolph.feature_builder.util.extension.getSelectionCenter
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionEnd
import com.deadrudolph.feature_builder.util.extension.setFocusTo
import com.deadrudolph.feature_builder.util.mapper.TextFieldStateListToTextAndChordsMapper
import com.deadrudolph.feature_builder_domain.domain.usecase.SaveSongUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.SongState
import com.deadrudolph.uicomponents.ui_model.TextFieldState
import com.deadrudolph.uicomponents.utils.helper.SongUICompositionHelper
import com.puls.stateutil.Result
import com.puls.stateutil.Result.Loading
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

internal class SongBuilderViewModelImpl @Inject constructor(
    private val textFieldStateListToTextAndChordsMapper: TextFieldStateListToTextAndChordsMapper,
    private val saveSongUseCase: SaveSongUseCase,
    private val getAllSongsUseCase: GetAllSongsUseCase
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
    override val currentSongState = MutableStateFlow(getInitialSongState())
    override val preCalculatedSongStateFlow = MutableStateFlow<SongItem?>(null)
    private val currentFields
        get() = currentSongState.value.textFields

    private var focusedTextFieldIndex = 0
    private var textLayoutResultList: ArrayList<TextLayoutResult?> = arrayListOf()
    private var calculatedTextLayoutResult: TextLayoutResult? = null

    override fun onTextChanged(index: Int, textValue: TextFieldValue) {
        focusedTextFieldIndex = index
        setFocusAndChords(index, textValue)
        mergeTextFieldWithPreviousIfNeeded()
    }

    override fun onNewChord() {
        chordPickerStateFlow.value = true
    }

    override fun setSong(songItem: SongItem) {
        preCalculatedSongStateFlow.value = songItem
    }

    override fun onChordSelected(chordType: ChordType) {
        chordPickerStateFlow.value = false
        val selectedTextFieldState = getSelectedTextFieldState() ?: kotlin.run {
            Timber.e("Focused value must not be null!")
            return
        }

        val selectionPosition = selectedTextFieldState.value.getSelectionCenter()
        val chordOffset = getHorizontalOffsetForCurrentSelection()
        val chord = ChordUIModel(
            chordType = chordType,
            horizontalOffset = chordOffset,
            position = selectionPosition
        )

        insertItemsIntoText(
            selectedTextFieldState = selectedTextFieldState,
            currentValueIndex = focusedTextFieldIndex,
            selectionCenterIndex = selectionPosition,
            chord = chord
        )
    }

    override fun onChordSelectionCancelled() {
        chordPickerStateFlow.value = false
        chordPickerForBlockStateFlow.value = null
    }

    override fun onTextFieldKeyBack(index: Int) {
        if (!textIsEmptyAt(index)) return
        if (singleTextFieldLeft()) {
            clearTextFields()
            return
        }

        setTextFields { textFields ->
            textFields.toMutableList()
                .apply { removeAt(index) }
                .setFocusTo(index.dec().coerceAtLeast(0))
        }
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
        setTextFields { textFieldStates ->
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
        setTextFields { textFieldStates ->
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
        setTextFields { textFields ->
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
        setTextFields { textFields ->
            textFields.mapIndexed { index, textFieldState ->
                if (index == indexAndChord.first) textFieldState.removeChord(indexAndChord.second)
                else textFieldState
            }
        }
        mergeTextFieldWithPreviousIfNeeded(indexAndChord.first)
    }

    override fun onChordRemovedFromBlock(blockIndex: Int, chordIndex: Int) {
        chordEditDialogForBlockStateFlow.value = null
        setTextFields { textFields ->
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
        val result = textFieldStateListToTextAndChordsMapper(
            currentFields
        )
        if (result.first.isBlank()) return
        viewModelScope.launch {
            saveSongUseCase.invoke(
                SongItem(
                    id = System.currentTimeMillis().toString(),
                    title = result.first.take(10),
                    imagePath = "",
                    text = result.first,
                    chords = result.second,
                    chordBlocks = currentSongState.value.textFields.mapNotNull { it.chordBlock }
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
        val textFields = SongUICompositionHelper.songItemToTextFieldsStateList(
            textLayoutResult = result,
            songItem = currentSong
        )
        currentSongState.value = SongState(
            title = currentSong.title,
            textFields = textFields,
            imagePath = currentSong.imagePath
        )
    }

    override fun onAddBlockClicked(title: String): Boolean {
        val selectedTextFieldState = getSelectedTextFieldState() ?: kotlin.run {
            Timber.e("Focused value must not be null!")
            return false
        }
        val selectionPosition = selectedTextFieldState.value.getSelectionCenter()

        val textLayoutResult = findTextLayoutResult(
            focusedTextFieldIndex
        ) ?: kotlin.run {
            Timber.e(
                "TextLayoutResult must not be null for text: ${
                    selectedTextFieldState.value.text
                }"
            )
            return false
        }

        if (
            textLayoutResult.getLineForOffset(selectionPosition) == 0 &&
            selectedTextFieldState.chordBlock != null
        ) return false

        insertItemsIntoText(
            selectedTextFieldState = selectedTextFieldState,
            currentValueIndex = focusedTextFieldIndex,
            selectionCenterIndex = selectionPosition,
            layoutResult = textLayoutResult,
            chordBlock = ChordBlock(
                index = focusedTextFieldIndex,
                chordsList = emptyList(),
                title = title
            )
        )
        return true
    }

    override fun onSongPickerDismissed() {
        songPickerDialogStateFlow.value = false
    }

    private fun clearTextFields() {
        currentSongState.value = getInitialSongState()
    }

    private fun singleTextFieldLeft(): Boolean {
        return currentFields.size <= 1
    }

    private fun textIsEmptyAt(index: Int): Boolean {
        return currentFields
            .getOrNull(index)
            ?.value
            ?.text
            ?.isEmpty() == true
    }

    private fun insertItemsIntoText(
        selectedTextFieldState: TextFieldState,
        currentValueIndex: Int,
        selectionCenterIndex: Int,
        layoutResult: TextLayoutResult? = null,
        chord: ChordUIModel? = null,
        chordBlock: ChordBlock? = null,
    ) {
        val selectedTextFieldValue = selectedTextFieldState.value

        val textLayoutResult = layoutResult ?: findTextLayoutResult(
            currentValueIndex
        ) ?: kotlin.run {
            Timber.e(
                "TextLayoutResult must not be null for text: ${
                    selectedTextFieldValue.text
                }"
            )
            return
        }

        val lineStart = textLayoutResult.getSelectedLineStartIndex(
            selectionCenterIndex
        )
        if (lineStart == 0) {
            /**
             * If the lineStart is 0 it means we are adding a chord
             * to the top line and we don't need to split the field
             * */
            addItemsToExistingField(chord, chordBlock)
            return
        }

        val firstSubString = selectedTextFieldValue.annotatedString
            .substring(0, lineStart.dec())

        val secondSubString = selectedTextFieldValue.annotatedString.substring(
            lineStart, selectedTextFieldValue.annotatedString.length
        )

        val firstTextFieldValue = TextFieldValue(
            text = firstSubString,
            selection = TextRange(0),
            composition = TextRange(0, firstSubString.length)
        )
        val secondTextFieldValue = selectedTextFieldValue.copy(
            text = secondSubString,
            selection = TextRange(
                selectedTextFieldValue.getSelectionCenter() - firstSubString.length
            ),
            composition = TextRange(
                0, secondSubString.length
            )
        )

        val currentTextFields = currentFields.toMutableList()

        currentTextFields.removeAt(focusedTextFieldIndex)
        currentTextFields.add(
            focusedTextFieldIndex,
            TextFieldState(
                isFocused = true,
                value = secondTextFieldValue,
                chordsList = chord?.let { newChord ->
                    listOf(
                        newChord.copy(
                            position = newChord.position - firstSubString.length
                        )
                    )
                }.orEmpty(),
                chordBlock = chordBlock
            )
        )
        currentTextFields.add(
            focusedTextFieldIndex,
            TextFieldState(
                isFocused = false,
                value = firstTextFieldValue,
                chordsList = selectedTextFieldState.chordsList,
                chordBlock = selectedTextFieldState.chordBlock
            )
        )
        textLayoutResultList.removeAt(focusedTextFieldIndex)
        textLayoutResultList.addAll(focusedTextFieldIndex, listOf(null, null))
        focusedTextFieldIndex += 1
        setTextFields { currentTextFields }
    }

    private fun addItemsToExistingField(chord: ChordUIModel?, block: ChordBlock?) {
        setTextFields { textFields ->
            textFields.toMutableList().apply {
                val element = get(focusedTextFieldIndex)
                set(
                    index = focusedTextFieldIndex,
                    element = element.copy(
                        chordsList = chord?.run {
                            element.chordsList.addChord(this)
                        } ?: element.chordsList,
                        chordBlock = block ?: element.chordBlock
                    )
                )
            }
        }
    }

    private fun List<ChordUIModel>.addChord(chord: ChordUIModel): List<ChordUIModel> {
        return toMutableList().apply {
            val index = indexOfFirst { it.horizontalOffset == chord.horizontalOffset }
            if (index == -1) add(chord)
            else set(index, chord)
        }
    }

    private fun TextFieldState.removeChord(chord: ChordUIModel): TextFieldState {
        return copy(
            chordsList = chordsList.toMutableList().apply {
                remove(chord)
            }
        )
    }

    private fun findTextLayoutResult(
        valueIndex: Int
    ): TextLayoutResult? {
        return textLayoutResultList.getOrNull(valueIndex)
    }

    override fun onLayoutResultChanged(textLayoutResult: TextLayoutResult, index: Int) {
        while (index > textLayoutResultList.size.dec()) {
            textLayoutResultList.add(null)
        }
        textLayoutResultList[index] = textLayoutResult
    }

    private fun setFocusAndChords(index: Int, textValue: TextFieldValue) {
        setTextFields { textFields ->
            textFields.mapIndexed { i, textFieldState ->
                if (i == index) textFieldState.copy(
                    isFocused = true,
                    value = textValue,
                    chordsList = textFieldState.chordsList
                ) else textFieldState.copy(
                    isFocused = false
                )
            }
        }
    }

    private fun getHorizontalOffsetForCurrentSelection(): Int {
        return textLayoutResultList.getOrNull(focusedTextFieldIndex)?.run {
            try {
                val currentTextField = currentFields.getOrNull(
                    focusedTextFieldIndex
                )
                val chordSelection = currentTextField?.value?.getSelectionCenter() ?: 0
                getHorizontalPosition(chordSelection, true).toInt()
            } catch (e: IllegalArgumentException) {
                Timber.e(e)
                0
            }
        } ?: 0
    }

    private fun mergeTextFieldWithPreviousIfNeeded(index: Int? = null) {

        val currentIndex = index ?: focusedTextFieldIndex
        val currentTextField = currentFields.getOrNull(
            currentIndex
        ) ?: return

        val isMergeNeeded = currentTextField.chordsList.isEmpty() &&
                currentTextField.chordBlock == null
        val isFirstField = currentIndex == 0
        val prevTextField = currentFields.getOrNull(
            currentIndex.dec()
        )
        if (!isMergeNeeded || isFirstField || prevTextField == null) return

        val newTextFieldValue = TextFieldValue(
            text = prevTextField.value.text + "\n" + currentTextField.value.text,
            selection = TextRange(
                prevTextField.value.text.length +
                        currentTextField.value.getTrueSelectionEnd().inc()
            )
        )

        val newTextFieldState = TextFieldState(
            value = newTextFieldValue,
            isFocused = true,
            chordsList = prevTextField.chordsList,
            chordBlock = prevTextField.chordBlock
        )

        val currentTextFieldsList = currentFields.toMutableList()
        currentTextFieldsList.removeAt(currentIndex)
        textLayoutResultList.removeAt(currentIndex)
        val nextFocus = currentIndex.dec()
        currentTextFieldsList[nextFocus] = newTextFieldState
        textLayoutResultList[nextFocus] = null
        setTextFields { currentTextFieldsList }
    }

    private fun getInitialSongState(): SongState =
        SongState(
            textFields = TextFieldState(
                value = TextFieldValue(),
                isFocused = true,
                chordsList = emptyList(),
            ).run(::listOf)
        )

    private fun setTextFields(transformFields: (List<TextFieldState>) -> List<TextFieldState>) {
        val currentSong = currentSongState.value
        currentSongState.value = currentSong.copy(
            textFields = transformFields(currentSong.textFields)
        )
    }

    private fun getSelectedTextFieldState(): TextFieldState? {
        return currentFields.getOrNull(
            focusedTextFieldIndex
        )
    }
}