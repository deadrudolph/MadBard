package com.deadrudolph.feature_builder.presentation.ui.screen

import android.util.Range
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionEnd
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionStart
import com.deadrudolph.feature_builder.util.extension.isSelected
import com.deadrudolph.home_domain.domain.model.songs_dashboard.Chord
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber

internal class SongBuilderViewModelImpl @Inject constructor(

) : SongBuilderViewModel() {

    //TODO: Remove temp fields
    override val textFieldsStateFlow = MutableStateFlow(
        listOf(
            TextFieldValue("qwekjbasdkfbvasdfkbasdfbnvadsfbnvadsfjbnvadsfbnvasdfjbvakjsdfvbnasdfvbadvbnasvnbavbfvaavsfavsvbnasdvbnfavf"),
            TextFieldValue("jhbsdfjhkasdjhasdjhfasghashjgaghjkafsghjasfghfasdgkafsghfaghafghfsg hafs 111 ghfasghafghasghjsfghjfsghjafsghjfsghafghafgh")
        )
    )
    override val chordsListStateFlow: MutableStateFlow<List<Chord>> = MutableStateFlow(listOf())
    override val chordPickerStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val focusedTextFieldIndex: MutableStateFlow<Int> = MutableStateFlow(0)
    private var textLayoutResultList: MutableList<TextLayoutResult> = arrayListOf()

    private var chordSelectionRange: Range<Int>? = null

    override fun onTextChanged(index: Int, textValue: TextFieldValue) {
        focusedTextFieldIndex.value = index
        textFieldsStateFlow.value = textFieldsStateFlow.value.mapIndexed { i, textFieldValue ->
            if (i == index) textValue
            else textFieldValue
        }
    }

    override fun onNewChord() {
        val currentValueIndex = focusedTextFieldIndex.value
        val currentEditingField = textFieldsStateFlow.value.getOrNull(
            currentValueIndex
        ) ?: kotlin.run {
            Timber.e("Current focused field must not be null")
            return
        }
        chordSelectionRange = Range(
            currentEditingField.getTrueSelectionStart(),
            currentEditingField.getTrueSelectionEnd()
        )
        setSelectionToEnd(currentEditingField, currentValueIndex)
        chordPickerStateFlow.value = true
    }

    private fun setSelectionToEnd(textFieldValue: TextFieldValue, index: Int) {
        textFieldsStateFlow.value = textFieldsStateFlow.value.map {
            if (it == textFieldValue) it.copy(
                selection = TextRange(
                    textFieldValue.getTrueSelectionEnd()
                )
            )
            else it.copy(selection = TextRange.Zero)
        }
    }

    override fun onChordSelected(chordType: ChordType) {
        chordPickerStateFlow.value = false
        val selectedRange = chordSelectionRange ?: kotlin.run {
            Timber.e("Selection range must not be null!")
            return
        }
        val selectedTextFieldValue = textFieldsStateFlow.value.getOrNull(
            focusedTextFieldIndex.value
        ) ?: kotlin.run {
            Timber.e("Focused value must not be null!")
            return
        }
        val wordCenterIndex = (selectedRange.upper - selectedRange.lower) / 2
        val chordPosition = selectedRange.lower + wordCenterIndex
        chordsListStateFlow.value = chordsListStateFlow.value + Chord(
            chordType = chordType,
            position = chordPosition
        )
        splitTextFields(
            selectedTextFieldValue = selectedTextFieldValue,
            currentValueIndex = focusedTextFieldIndex.value,
            selectionCenterIndex = chordPosition
        )
    }

    override val testTextField: MutableStateFlow<TextFieldValue> =
        MutableStateFlow(TextFieldValue())

    private fun splitTextFields(
        selectedTextFieldValue: TextFieldValue,
        currentValueIndex: Int,
        selectionCenterIndex: Int
    ) {
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
        ) ?: return

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
                selectedTextFieldValue.annotatedString.length - firstSubString.length
            ),
            composition = TextRange(
                0, secondSubString.length
            )
        )

        val currentTextFields = textFieldsStateFlow.value.toMutableList()

        val index = currentTextFields.indexOf(selectedTextFieldValue)
        currentTextFields.removeAt(index)
        currentTextFields.add(index, secondTextFieldValue)
        currentTextFields.add(index, firstTextFieldValue)
        focusedTextFieldIndex.value = focusedTextFieldIndex.value.inc()
        textFieldsStateFlow.value = currentTextFields
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

    override fun onLayoutResultChanged(textLayoutResult: TextLayoutResult, index: Int) {
        if (index in textLayoutResultList.indices)
            textLayoutResultList[index] = textLayoutResult
        else textLayoutResultList.add(textLayoutResult)
    }
}