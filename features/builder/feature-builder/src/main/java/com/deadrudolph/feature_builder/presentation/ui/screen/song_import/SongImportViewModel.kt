package com.deadrudolph.feature_builder.presentation.ui.screen.song_import

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

internal abstract class SongImportViewModel : ViewModel() {

    abstract val textFieldState: StateFlow<TextFieldValue>

    abstract fun onTextValueChanged(value: TextFieldValue)

    abstract fun onTextLayoutResultChanged(result: TextLayoutResult)

    abstract fun onSongAnalyzeClicked()
}