package com.deadrudolph.feature_builder.presentation.ui.screen.song_import

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordBlock
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.ui_model.RawStringType.CHORD
import com.deadrudolph.feature_builder.ui_model.RawStringType.CHORD_BLOCK
import com.deadrudolph.feature_builder.ui_model.RawStringType.TEXT
import com.deadrudolph.feature_builder.util.extension.getChordBlock
import com.deadrudolph.feature_builder.util.extension.getChordsList
import com.deadrudolph.feature_builder.util.extension.getRawStringType
import com.puls.stateutil.Result
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

internal class SongImportViewModelImpl @Inject constructor() : SongImportViewModel() {

    override val textFieldState = MutableStateFlow(TextFieldValue())
    override val analyzedSongState = MutableStateFlow<Result<SongItem>>(Result.Loading(false))

    private var textLayoutResult: TextLayoutResult? = null

    override fun onTextValueChanged(value: TextFieldValue) {
        textFieldState.value = value
    }

    override fun onTextLayoutResultChanged(result: TextLayoutResult) {
        textLayoutResult = result
    }

    override fun analyzeSong() {
        val textLinesArray = getCurrentTextLines().ifEmpty {
            Timber.d("Song is Empty!")
            return
        }
        viewModelScope.launch(Dispatchers.Default) {
            analyzedSongState.value = Result.Loading(true)
            val title = textLinesArray.first()
            val textFields = arrayListOf<String>()
            val chords = arrayListOf<Chord>()
            val blocks = arrayListOf<ChordBlock>()
            val currentTextField = StringBuilder()
            textLinesArray.forEachIndexed { index, line ->
                when (line.getRawStringType()) {
                    TEXT -> {
                        currentTextField.append(line)
                    }

                    CHORD_BLOCK -> {
                        if (currentTextField.isNotBlank()) {
                            textFields.add(currentTextField.toString())
                            currentTextField.clear()
                        }
                        blocks.add(line.getChordBlock(textFields.size))
                    }

                    CHORD -> {
                        chords.addAll(
                            line.getChordsList(
                                textLayoutResult = textLayoutResult,
                                prevTextSize = textLinesArray.take(index).sumOf { it.length },
                                allLines = textLinesArray,
                                currentLineIndex = index
                            )
                        )
                        if (currentTextField.isNotBlank()) {
                            textFields.add(currentTextField.toString())
                            currentTextField.clear()
                        }
                    }
                }

            }
            if (currentTextField.isNotBlank()) {
                textFields.add(currentTextField.toString())
                currentTextField.clear()
            }
            analyzedSongState.value = Result.Success(
                SongItem(
                    id = System.currentTimeMillis().toString(),
                    title = title,
                    imagePath = "",
                    text = if (textFields.isEmpty()) String()
                    else textFields.reduce { prev, next -> prev + next },
                    chords = chords,
                    chordBlocks = blocks
                )
            )
        }
    }

    private fun getCurrentTextLines(): List<String> {
        val chars = arrayListOf<Pair<Int, Char>>()
        val currentText = textFieldState.value.text
        currentText.forEachIndexed { index, value ->
            textLayoutResult?.let { result ->
                chars.add(result.getLineForOffset(index) to value)
            }
        }
        return chars.groupBy { it.first }.toSortedMap().map { value ->
            String(value.value.map { it.second }.toCharArray())
        }
    }
}