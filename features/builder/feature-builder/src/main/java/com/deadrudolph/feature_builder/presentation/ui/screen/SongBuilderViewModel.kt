package com.deadrudolph.feature_builder.presentation.ui.screen

import android.graphics.PointF
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.home_domain.domain.model.songs_dashboard.Chord
import kotlinx.coroutines.flow.StateFlow

internal abstract class SongBuilderViewModel : ViewModel() {

    abstract val textStateFlow: StateFlow<TextFieldValue>

    abstract val chordsListStateFlow: StateFlow<List<Chord>>

    abstract val chordPickerStateFlow: StateFlow<Boolean>

    abstract fun getCoordsForPosition(position: Int): PointF?

    abstract fun onTextChanged(textValue: TextFieldValue)

    abstract fun onNewChord()

    abstract fun onChordSelected(chordType: ChordType)

    abstract fun onLayoutResultChanged(textLayoutResult: TextLayoutResult)
}