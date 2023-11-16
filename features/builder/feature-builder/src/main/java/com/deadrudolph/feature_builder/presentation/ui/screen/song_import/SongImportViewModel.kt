package com.deadrudolph.feature_builder.presentation.ui.screen.song_import

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.ui_model.ToastType
import com.puls.stateutil.Result
import kotlinx.coroutines.flow.StateFlow

internal abstract class SongImportViewModel : ViewModel() {

    abstract val textFieldState: StateFlow<TextFieldValue>

    abstract val analyzedSongState: StateFlow<Result<SongItem>>

    abstract val toastMessageState: StateFlow<ToastType?>

    abstract fun onTextValueChanged(value: TextFieldValue)

    abstract fun onTextLayoutResultChanged(result: TextLayoutResult)

    abstract fun analyzeSong()

    abstract fun onToastShown()
}
