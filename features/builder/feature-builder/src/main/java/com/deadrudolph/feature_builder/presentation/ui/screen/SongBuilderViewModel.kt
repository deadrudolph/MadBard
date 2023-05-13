package com.deadrudolph.feature_builder.presentation.ui.screen

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.feature_builder.presentation.ui.model.ChordUIModel
import com.deadrudolph.feature_builder.presentation.ui.model.TextFieldState
import kotlinx.coroutines.flow.StateFlow

internal abstract class SongBuilderViewModel : ViewModel() {

    abstract val textFieldsStateFlow: StateFlow<List<TextFieldState>>

    abstract val chordPickerStateFlow: StateFlow<Boolean>

    abstract val chordEditDialogStateFlow: StateFlow<Pair<Int, ChordUIModel>?>

    abstract fun onTextChanged(index: Int, textValue: TextFieldValue)

    abstract fun onLayoutResultChanged(textLayoutResult: TextLayoutResult, index: Int)

    abstract fun onNewChord()

    abstract fun onChordSelected(chordType: ChordType)

    abstract fun onChordSelectionCancelled()

    abstract fun onTextFieldKeyBack(index: Int)

    abstract fun onChordClicked(fieldIndex: Int, chord: ChordUIModel)

    abstract fun onChordEditorDismissed()

    abstract fun onChordRemoved(indexAndChord: Pair<Int, ChordUIModel>)
}