package com.deadrudolph.feature_builder.util.manager

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.util.extension.addChord
import com.deadrudolph.feature_builder.util.extension.getSelectedLineStartIndex
import com.deadrudolph.feature_builder.util.extension.getSelectionCenter
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionEnd
import com.deadrudolph.feature_builder.util.extension.setFocusTo
import com.deadrudolph.uicomponents.ui_model.ChordBlockUIModel
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.SongState
import com.deadrudolph.uicomponents.ui_model.TextFieldState
import com.deadrudolph.uicomponents.utils.helper.SongUICompositionHelper
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

class SongBuilderTextFieldsManager {

    private var focusedTextFieldIndex = 0

    private var textLayoutResultList: ArrayList<TextLayoutResult?> = arrayListOf()

    val currentSongState = MutableStateFlow(getInitialSongState())
    val currentFields
        get() = currentSongState.value.textFields

    fun setFocusAndChords(
        index: Int,
        textValue: TextFieldValue
    ) {
        setTextFields { textFields ->
            textFields.mapIndexed { i, textFieldState ->
                if (i == index) textFieldState.copy(
                    isFocused = true,
                    value = textValue,
                    chordsList = textFieldState.chordsList.adjustOffsets(textValue)
                ) else textFieldState.copy(
                    isFocused = false
                )
            }
        }
    }

    fun onChordSelected(chordType: ChordType) {
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

    fun setFocus(index: Int) {
        focusedTextFieldIndex = index
    }

    fun applyToLayoutResult(operation: ArrayList<TextLayoutResult?>.() -> Unit) {
        textLayoutResultList.operation()
    }

    fun setTextFields(transformFields: (List<TextFieldState>) -> List<TextFieldState>) {
        val currentSong = currentSongState.value
        currentSongState.value = currentSong.copy(
            textFields = transformFields(currentSong.textFields)
        )
    }

    fun calculateSong(songToCalculate: SongItem, result: TextLayoutResult) {
        val textFields = SongUICompositionHelper.songItemToTextFieldsStateList(
            textLayoutResult = result,
            songItem = songToCalculate
        )
        currentSongState.value = SongState(
            title = songToCalculate.title,
            textFields = textFields,
            imagePath = songToCalculate.imagePath
        )
    }

    fun insertBlock(title: String): Boolean {
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
            chordBlock = ChordBlockUIModel(
                chordsList = emptyList(),
                title = title,
                fieldIndex = focusedTextFieldIndex
            )
        )
        return true
    }

    fun mergeTextFieldWithPreviousIfNeeded(index: Int? = null) {

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

    fun tryRemoveTextField(index: Int) {
        if (!textIsEmptyAt(index)) return
        if (isSingleTextFieldLeft()) {
            clearTextFields()
            return
        }

        setTextFields { textFields ->
            textFields.toMutableList()
                .apply { removeAt(index) }
                .setFocusTo(index.dec().coerceAtLeast(0))
        }
    }

    private fun getInitialSongState(): SongState =
        SongState(
            textFields = TextFieldState(
                value = TextFieldValue(),
                isFocused = true,
                chordsList = emptyList(),
            ).run(::listOf)
        )

    private fun getSelectedTextFieldState(): TextFieldState? {
        return currentFields.getOrNull(
            focusedTextFieldIndex
        )
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

    private fun insertItemsIntoText(
        selectedTextFieldState: TextFieldState,
        currentValueIndex: Int,
        selectionCenterIndex: Int,
        layoutResult: TextLayoutResult? = null,
        chord: ChordUIModel? = null,
        chordBlock: ChordBlockUIModel? = null,
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

    private fun findTextLayoutResult(
        valueIndex: Int
    ): TextLayoutResult? {
        return textLayoutResultList.getOrNull(valueIndex)
    }

    private fun addItemsToExistingField(chord: ChordUIModel?, block: ChordBlockUIModel?) {
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

    private fun isSingleTextFieldLeft(): Boolean {
        return currentFields.size <= 1
    }

    private fun textIsEmptyAt(index: Int): Boolean {
        return currentFields
            .getOrNull(index)
            ?.value
            ?.text
            ?.isEmpty() == true
    }

    private fun clearTextFields() {
        currentSongState.value = getInitialSongState()
    }
}

private fun List<ChordUIModel>.adjustOffsets(
    textValue: TextFieldValue
): List<ChordUIModel> {
    return map { chord ->
        val firstLine = textValue.text.lines().firstOrNull().orEmpty()
        if (chord.position > firstLine.length) {
            chord.copy(
                positionOverlapCharCount = chord.position - firstLine.length
            )
        } else chord.copy(positionOverlapCharCount = 0)
    }
}
