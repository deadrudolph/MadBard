package com.deadrudolph.feature_builder.presentation.ui.screen.song_import

import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordBlock
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.feature_builder.ui_model.RawStringType
import com.deadrudolph.feature_builder.ui_model.RawStringType.CHORD
import com.deadrudolph.feature_builder.ui_model.RawStringType.CHORD_BLOCK
import com.deadrudolph.feature_builder.ui_model.RawStringType.TEXT
import com.deadrudolph.feature_builder.util.extension.getChordBlock
import com.deadrudolph.feature_builder.util.extension.getChordTypesList
import com.deadrudolph.feature_builder.util.extension.getChordsList
import com.deadrudolph.feature_builder.util.extension.getRawStringType
import com.deadrudolph.uicomponents.utils.logslogs
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
        val lineTypesArray = textLinesArray.map { line ->
            line.getRawStringType()
        }.toMutableList()

        viewModelScope.launch(Dispatchers.Default) {
            analyzedSongState.value = Result.Loading(true)
            val title = textLinesArray.first()
            val textFields = arrayListOf<String>()
            val chords = arrayListOf<Chord>()
            val blocks = arrayListOf<ChordBlock>()
            val currentTextField = StringBuilder()

            textLinesArray.forEachIndexed { index, line ->
                when (lineTypesArray[index]) {
                    TEXT -> {
                        currentTextField.append(line)
                    }

                    CHORD_BLOCK -> {
                        textFields.addTextIfNotBlank(currentTextField)
                        blocks.add(line.getChordBlock(
                            textFields.sumOf { it.length }
                        ))
                    }

                    CHORD -> {
                        textFields.addTextIfNotBlank(currentTextField)
                        if (isContinuesChordsBlock(
                                index = index,
                                lineTypesArray = lineTypesArray,
                                textLinesArray = textLinesArray
                            )
                        ) {
                            blocks.addChordsToLastBlock(line.getChordTypesList())
                            lineTypesArray[index] = CHORD_BLOCK
                        } else {
                            chords.addAll(
                                line.getChordsList(
                                    textLayoutResult = textLayoutResult,
                                    prevTextSize = textLinesArray.take(index).sumOf { it.length },
                                    allLines = textLinesArray,
                                    currentLineIndex = index
                                )
                            )
                        }
                    }
                }

            }
            textFields.addTextIfNotBlank(currentTextField)
            logslogs("Size: ${textFields.size}, Fourth: ${textFields[4]}")
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

    private fun isContinuesChordsBlock(
        index: Int,
        lineTypesArray: MutableList<RawStringType>,
        textLinesArray: List<String>
    ): Boolean {
        val isPrevTypeBlock = lineTypesArray.getOrNull(index.dec()) == CHORD_BLOCK
        val nextLineType = lineTypesArray.getOrNull(index.inc())
        val nextLine = textLinesArray.getOrNull(index.inc())
        val isNextLineInvalidForChord = nextLine == null || (nextLineType == TEXT
                && nextLine.isBlank()) || nextLineType == CHORD
        return isPrevTypeBlock && isNextLineInvalidForChord
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

private fun ArrayList<ChordBlock>.addChordsToLastBlock(chords: List<ChordType>) {
    if (isEmpty()) return
    val lastItem = last()
    set(
        index = indices.last,
        lastItem.copy(
            chordsList = lastItem.chordsList + chords
        )
    )
}

private fun MutableList<String>.addTextIfNotBlank(text: StringBuilder) {
    if (text.isNotBlank()) {
        add(text.toString())
        text.clear()
    }
}
