package com.deadrudolph.feature_builder.presentation.ui.screen

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.feature_builder.presentation.ui.model.ChordUIModel
import com.deadrudolph.feature_builder.presentation.ui.model.TextFieldState
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionEnd
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionStart
import com.deadrudolph.feature_builder.util.extension.setFocusTo
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

internal class SongBuilderViewModelImpl @Inject constructor(

) : SongBuilderViewModel() {

    //TODO: Remove temp fields
    override val textFieldsStateFlow = MutableStateFlow(
        listOf(
            textFieldState(index = 0),
            textFieldState(true, 1),
            textFieldState(index = 2),
            textFieldState(index = 3)
        )
    )

    override val chordPickerStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var focusedTextFieldIndex = 0
    private var textLayoutResultList: ArrayList<TextLayoutResult?> = arrayListOf()

    override fun onTextChanged(index: Int, textValue: TextFieldValue) {
        focusedTextFieldIndex = index
        setFocusAndChords(index, textValue)
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
        if (!textIsEmptyAt(index) || singleTextFieldLeft()) return
        textFieldsStateFlow.value = textFieldsStateFlow
            .value
            .toMutableList()
            .apply { removeAt(index) }
            .setFocusTo(index.dec().coerceAtLeast(0))
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

        val lineStart = getSelectedLineStartIndex(
            textLayoutResult, selectedTextFieldValue, selectionCenterIndex
        ) ?: kotlin.run {
            /**
             * If the lineStart is null it means we are adding a chord
             * to the top line and we don't need to split the field
             * */
            addChordToExistingField(chord)
            return
        }

        val firstSubString = selectedTextFieldValue.annotatedString.substring(0, lineStart.dec())
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
                        chordsList = element.chordsList + chord
                    )
                )
            }
    }

    private fun getSelectedLineStartIndex(
        textLayoutResult: TextLayoutResult,
        selectedTextFieldValue: TextFieldValue,
        selectionCenterIndex: Int
    ): Int? {
        val currentLine = textLayoutResult.getLineForOffset(
            selectionCenterIndex.inc()
        )
        val text = selectedTextFieldValue.annotatedString
        for (i in (text.indices.first..selectionCenterIndex).reversed()) {
            if (textLayoutResult.getLineForOffset(i) < currentLine) {
                return i.inc()
            }
        }

        return null
    }

    private fun findTextLayoutResult(
        valueIndex: Int
    ): TextLayoutResult? {
        return textLayoutResultList.getOrNull(valueIndex)
    }

    private fun TextFieldValue.getSelectionCenter(): Int {
        val trueStart = getTrueSelectionStart()
        val trueEnd = getTrueSelectionEnd()
        return (trueStart + ((trueEnd - trueStart) / 2)).dec().coerceAtLeast(0)
    }

    override fun onLayoutResultChanged(textLayoutResult: TextLayoutResult, index: Int) {
        while (index > textLayoutResultList.size.dec()) {
            textLayoutResultList.add(null)
        }
        textLayoutResultList[index] = textLayoutResult
    }

    private fun textFieldState(isFocused: Boolean = false, index: Int): TextFieldState {
        return TextFieldState(
            isFocused = isFocused,
            value = TextFieldValue("$index jhbsdfjhkasdjhasdjhfasghashjgaghjkafsghjasfghfasdgkafsghfaghafghfsg hafs 111 ghfasghafghasghjsfghjfsghjafsghjfsghafghafgh"),
            chordsList = listOf()
        )
    }

    private fun setFocusAndChords(index: Int, textValue: TextFieldValue) {
        textFieldsStateFlow.value = textFieldsStateFlow.value.mapIndexed { i, textFieldState ->
            val chords = filterChordsByCurrentPosition(
                textFieldState.value.text.length,
                textFieldState.chordsList
            )
            if (i == index) TextFieldState(
                isFocused = true,
                value = textValue,
                chordsList = chords
            ) else textFieldState.copy(isFocused = false)
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
}