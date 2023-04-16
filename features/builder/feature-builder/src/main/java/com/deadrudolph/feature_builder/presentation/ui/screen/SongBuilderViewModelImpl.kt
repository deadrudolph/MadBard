package com.deadrudolph.feature_builder.presentation.ui.screen

import android.graphics.PointF
import androidx.compose.ui.text.*
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.sp
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.home_domain.domain.model.songs_dashboard.Chord
import com.deadrudolph.uicomponents.utils.logslogs
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.abs

internal class SongBuilderViewModelImpl @Inject constructor(

) : SongBuilderViewModel() {

    override val textStateFlow = MutableStateFlow(TextFieldValue())
    override val chordsListStateFlow: MutableStateFlow<List<Chord>> = MutableStateFlow(listOf())
    override val chordPickerStateFlow: MutableStateFlow<Boolean> = MutableStateFlow(false)
    private var currentTextLayoutResult: TextLayoutResult? = null

    override fun onTextChanged(textValue: TextFieldValue) {
        textStateFlow.value = textValue
    }

    override fun onNewChord() {
        chordPickerStateFlow.value = true
    }

    override fun onChordSelected(chordType: ChordType) {
        chordPickerStateFlow.value = false
        val selectedRange = textStateFlow.value.selection
        val wordCenterIndex = abs(selectedRange.end - selectedRange.start) / 2
        val chordPosition = selectedRange.end.coerceAtMost(selectedRange.start) + wordCenterIndex
        chordsListStateFlow.value = chordsListStateFlow.value + Chord(
            chordType = chordType,
            position = chordPosition
        )
        textStateFlow.value = textStateFlow.value.copy(
            selection = TextRange(
                textStateFlow.value.annotatedString.length
            )
        )
    }

    override fun getCoordsForPosition(position: Int): PointF? {
        return if (position !in textStateFlow.value.annotatedString.text.indices) {
            chordsListStateFlow.value = chordsListStateFlow.value.filter {
                it.position != position
            }
            Timber.e("Position if the chord is not withing text length: $position")
            null
        } else {
            currentTextLayoutResult?.getBoundingBox(position)?.let { rect ->
                logslogs("Rect: ${rect.bottom}")
                PointF(
                    rect.left,
                    rect.top
                )
            }
        }
    }

    override fun onLayoutResultChanged(textLayoutResult: TextLayoutResult) {
        currentTextLayoutResult = textLayoutResult
    }
}