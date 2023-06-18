package com.deadrudolph.feature_builder.presentation.ui.screen

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.util.extension.getSelectedLineStartIndex
import com.deadrudolph.feature_builder.util.extension.getSelectionCenter
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionEnd
import com.deadrudolph.feature_builder.util.extension.setFocusTo
import com.deadrudolph.feature_builder.util.mapper.TextFieldStateListToTextAndChordsMapper
import com.deadrudolph.feature_builder_domain.domain.usecase.SaveSongUseCase
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.TextFieldState
import com.deadrudolph.uicomponents.utils.logslogs
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

internal class SongBuilderViewModelImpl @Inject constructor(
    private val textFieldStateListToTextAndChordsMapper: TextFieldStateListToTextAndChordsMapper,
    private val saveSongUseCase: SaveSongUseCase
) : SongBuilderViewModel() {

    override val textFieldsStateFlow = MutableStateFlow(getInitialTextFieldList())

    override val chordPickerStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val chordEditDialogStateFlow = MutableStateFlow<Pair<Int, ChordUIModel>?>(null)
    private var focusedTextFieldIndex = 0
    private var textLayoutResultList: ArrayList<TextLayoutResult?> = arrayListOf()

    override fun onTextChanged(index: Int, textValue: TextFieldValue) {
        focusedTextFieldIndex = index
        setFocusAndChords(index, textValue)
        mergeTextFieldWithPreviousIfNeeded()
    }

    override fun onNewChord() {
        chordPickerStateFlow.value = true
    }

    override fun onChordSelected(chordType: ChordType) {
        chordPickerStateFlow.value = false
        val selectedTextFieldState = textFieldsStateFlow.value.getOrNull(
            focusedTextFieldIndex
        ) ?: kotlin.run {
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

        insertChordIntoText(
            selectedTextFieldState = selectedTextFieldState,
            currentValueIndex = focusedTextFieldIndex,
            selectionCenterIndex = selectionPosition,
            chord = chord
        )
    }

    override fun onChordSelectionCancelled() {
        chordPickerStateFlow.value = false
    }

    override fun onTextFieldKeyBack(index: Int) {
        if (!textIsEmptyAt(index)) return
        if (singleTextFieldLeft()) {
            clearTextFields()
            return
        }
        textFieldsStateFlow.value = textFieldsStateFlow
            .value
            .toMutableList()
            .apply { removeAt(index) }
            .setFocusTo(index.dec().coerceAtLeast(0))
    }

    override fun onChordClicked(fieldIndex: Int, chord: ChordUIModel) {
        chordEditDialogStateFlow.value = fieldIndex to chord
    }

    override fun onChordEditorDismissed() {
        chordEditDialogStateFlow.value = null
    }

    override fun onChordRemoved(indexAndChord: Pair<Int, ChordUIModel>) {
        chordEditDialogStateFlow.value = null
        textFieldsStateFlow.value = textFieldsStateFlow.value.mapIndexed { index, textFieldState ->
            if (index == indexAndChord.first) textFieldState.removeChord(indexAndChord.second)
            else textFieldState
        }
        mergeTextFieldWithPreviousIfNeeded(indexAndChord.first)
    }

    override fun onSaveSongClicked() {
        val result = textFieldStateListToTextAndChordsMapper(
            textFieldsStateFlow.value
        )
        if (result.first.isBlank()) return
        viewModelScope.launch {
            saveSongUseCase.invoke(
                SongItem(
                    id = System.currentTimeMillis().toString(),
                    title = result.first.take(10),
                    imagePath = "",
                    text = result.first,
                    chords = result.second
                )
            )
        }
    }

    override fun onAddSongClicked() {

    }

    private fun clearTextFields() {
        textFieldsStateFlow.value = getInitialTextFieldList()
    }

    private fun singleTextFieldLeft(): Boolean {
        return textFieldsStateFlow.value.size <= 1
    }

    private fun textIsEmptyAt(index: Int): Boolean {
        return textFieldsStateFlow.value
            .getOrNull(index)
            ?.value
            ?.text
            ?.isEmpty() == true
    }

    private fun insertChordIntoText(
        selectedTextFieldState: TextFieldState,
        currentValueIndex: Int,
        selectionCenterIndex: Int,
        chord: ChordUIModel
    ) {
        val selectedTextFieldValue = selectedTextFieldState.value
        val textLayoutResult = findTextLayoutResult(currentValueIndex) ?: kotlin.run {
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
        if (lineStart == 0){
            /**
             * If the lineStart is 0 it means we are adding a chord
             * to the top line and we don't need to split the field
             * */
            addChordToExistingField(chord)
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

        val currentTextFields = textFieldsStateFlow.value.toMutableList()

        currentTextFields.removeAt(focusedTextFieldIndex)
        currentTextFields.add(
            focusedTextFieldIndex,
            TextFieldState(
                isFocused = true,
                value = secondTextFieldValue,
                chordsList = listOf(
                    chord.copy(
                        position = chord.position - firstSubString.length
                    )
                )
            )
        )
        currentTextFields.add(
            focusedTextFieldIndex,
            TextFieldState(
                isFocused = false,
                value = firstTextFieldValue,
                chordsList = selectedTextFieldState.chordsList
            )
        )
        textLayoutResultList.removeAt(focusedTextFieldIndex)
        textLayoutResultList.addAll(focusedTextFieldIndex, listOf(null, null))
        focusedTextFieldIndex += 1
        textFieldsStateFlow.value = currentTextFields
    }

    private fun addChordToExistingField(chord: ChordUIModel) {
        textFieldsStateFlow.value = textFieldsStateFlow
            .value
            .toMutableList()
            .apply {
                val element = get(focusedTextFieldIndex)
                set(
                    index = focusedTextFieldIndex,
                    element = element.copy(
                        chordsList = element.chordsList.addChord(chord)
                    )
                )
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
        textFieldsStateFlow.value = textFieldsStateFlow.value.mapIndexed { i, textFieldState ->
            if (i == index) textFieldState.copy(
                isFocused = true,
                value = textValue,
                chordsList = filterChordsByCurrentPosition(
                    textFieldState.value.annotatedString.lines().first().length,
                    textFieldState.chordsList
                )
            ) else textFieldState.copy(
                isFocused = false
            )
        }
    }

    private fun filterChordsByCurrentPosition(
        stringLength: Int,
        chords: List<ChordUIModel>
    ): List<ChordUIModel> {
        return if (chords.all { it.position <= stringLength }) chords
        else chords.filter { it.position <= stringLength }
    }

    private fun getHorizontalOffsetForCurrentSelection(): Int {
        return textLayoutResultList.getOrNull(focusedTextFieldIndex)?.run {
            try {
                val currentTextField = textFieldsStateFlow.value.getOrNull(
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
        val currentTextField = textFieldsStateFlow.value.getOrNull(
            currentIndex
        ) ?: return

        val isMergeNeeded = currentTextField.chordsList.isEmpty()
        val isFirstField = currentIndex == 0
        val prevTextField = textFieldsStateFlow.value.getOrNull(
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
        )

        val currentTextFieldsList = textFieldsStateFlow.value.toMutableList()
        currentTextFieldsList.removeAt(currentIndex)
        textLayoutResultList.removeAt(currentIndex)
        val nextFocus = currentIndex.dec()
        currentTextFieldsList[nextFocus] = newTextFieldState
        textLayoutResultList[nextFocus] = null
        textFieldsStateFlow.value = currentTextFieldsList
    }

    private fun getInitialTextFieldList(): List<TextFieldState> {
        return TextFieldState(
            value = TextFieldValue(),
            isFocused = true,
            chordsList = emptyList()
        ).run(::listOf)
    }
}