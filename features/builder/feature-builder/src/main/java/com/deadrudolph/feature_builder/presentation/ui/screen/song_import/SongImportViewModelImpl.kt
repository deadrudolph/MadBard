package com.deadrudolph.feature_builder.presentation.ui.screen.song_import

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

internal class SongImportViewModelImpl @Inject constructor() : SongImportViewModel() {

    override val textFieldState = MutableStateFlow(TextFieldValue())

    override fun onTextValueChanged(value: TextFieldValue) {
        textFieldState.value = value
    }

    override fun onTextLayoutResultChanged(result: TextLayoutResult) {

    }

    override fun onSongAnalyzeClicked() {

    }
}