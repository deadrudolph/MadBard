package com.deadrudolph.feature_builder.presentation.ui.screen.song_builder

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.feature_builder.R
import com.deadrudolph.feature_builder.di.component.SongBuilderComponentHolder
import com.deadrudolph.feature_builder.presentation.ui.dialog.ChordEditDialog
import com.deadrudolph.feature_builder.presentation.ui.dialog.ChordsListDialog
import com.deadrudolph.feature_builder.presentation.ui.dialog.ConfirmationDialog
import com.deadrudolph.feature_builder.presentation.ui.dialog.TextEditDialog
import com.deadrudolph.feature_builder.presentation.ui.view.SongBuilderControls
import com.deadrudolph.feature_builder.presentation.ui.view.SongPickerDialog
import com.deadrudolph.feature_builder.presentation.ui.view.SongTextEditorView
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme
import com.deadrudolph.uicomponents.compose.view.TextFieldForCalculation

internal class SongBuilderScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val songBuilderViewModel =
            getDaggerViewModel<SongBuilderViewModel>(
                viewModelProviderFactory = SongBuilderComponentHolder.getInternal()
                    .getViewModelFactory(),
                isSharedViewModel = true
            )

        DefaultTheme {
            songBuilderViewModel
                .preCalculatedSongStateFlow
                .collectAsState()
                .value
                ?.let { songItem ->
                    TextFieldForCalculation(
                        songItem = songItem,
                        onTextLayoutResult = songBuilderViewModel::onCalculatedTextLayoutResult
                    )
                }

            val songPickerState = songBuilderViewModel
                .songPickerDialogStateFlow
                .collectAsState()
                .value

            if (songPickerState) {
                SongPickerDialog(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp, vertical = 60.dp),
                    onDismiss = {
                        songBuilderViewModel.onSongPickerDismissed()
                    },
                    onSongSelected = songBuilderViewModel::onSongSelected,
                    songsState = songBuilderViewModel.songListState
                )
            }

            Column(modifier = Modifier.fillMaxSize()) {
                val chordsPickerState = songBuilderViewModel
                    .chordPickerStateFlow.collectAsState()
                val chordsPickerForBlockState = songBuilderViewModel
                    .chordPickerForBlockStateFlow.collectAsState()
                val chordEditState = songBuilderViewModel
                    .chordEditDialogStateFlow.collectAsState()
                val chordEditForBlockState = songBuilderViewModel
                    .chordEditDialogForBlockStateFlow.collectAsState()
                val textEditDialogState = songBuilderViewModel
                    .textEditDialogStateFlow.collectAsState()
                val chordRemoveConfirmationState = songBuilderViewModel
                    .chordBlockDeleteConfirmationDialogState.collectAsState()

                val context = LocalContext.current

                SongBuilderControls(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    onNewChord = {
                        if(!songBuilderViewModel.onNewChord()) {
                            Toast.makeText(
                                context,
                                R.string.error_chord_add,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    onSaveSong = songBuilderViewModel::onSaveSongClicked,
                    onAddSong = songBuilderViewModel::onAddSongClicked,
                    onNewBlock = { title ->
                        val isBlockAdded = songBuilderViewModel.onAddBlockClicked(title)
                        if (!isBlockAdded) {
                            Toast.makeText(
                                context,
                                R.string.error_chord_block_insertion,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )

                Box(modifier = Modifier.fillMaxSize()) {

                    SongTextEditorView(
                        modifier = Modifier.fillMaxSize(),
                        onTextStateChanged = { index, value ->
                            songBuilderViewModel.onTextChanged(index, value)
                        },
                        songState = songBuilderViewModel.currentSongState,
                        onTextLayoutResultChanged = { index, value ->
                            songBuilderViewModel.onLayoutResultChanged(value, index)
                        },
                        onKeyBack = songBuilderViewModel::onTextFieldKeyBack,
                        onCommonChordClicked = songBuilderViewModel::onCommonChordClicked,
                        onChordsBlockChordClicked = songBuilderViewModel::onChordsBlockChordClicked,
                        onChordBlockTextClicked = songBuilderViewModel::onChordsBlockTextClicked,
                        onChordBlockAddChordClicked = songBuilderViewModel::onChordBlockAddChordClicked,
                        onChordBlockRemoveClicked = songBuilderViewModel::onChordBlockDeleteClicked,
                        onChordOffsetsChanged = songBuilderViewModel::onChordOffsetsChanged
                    )

                    if (chordsPickerState.value) {
                        ChordsListDialog(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 60.dp)
                                .align(Alignment.Center),
                            onDismiss = songBuilderViewModel::onChordSelectionCancelled,
                            onChordSelected = songBuilderViewModel::onChordSelected
                        )
                    }

                    chordsPickerForBlockState.value?.let { index ->
                        ChordsListDialog(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(horizontal = 30.dp, vertical = 60.dp)
                                .align(Alignment.Center),
                            onDismiss = songBuilderViewModel::onChordSelectionCancelled,
                            onChordSelected = { chordType ->
                                songBuilderViewModel.onChordBlockChordSelected(
                                    index = index,
                                    chordType = chordType
                                )
                            }
                        )
                    }

                    chordEditState.value?.let { indexAndChord ->
                        ChordEditDialog(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(top = 50.dp)
                                .align(Alignment.TopCenter),
                            onDismiss = songBuilderViewModel::onChordEditorDismissed,
                            onChordRemoved = {
                                songBuilderViewModel.onChordRemoved(indexAndChord)
                            },
                            chordType = indexAndChord.second.chordType
                        )
                    }

                    chordEditForBlockState.value?.let { indexAndChord ->
                        ChordEditDialog(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(top = 50.dp)
                                .align(Alignment.TopCenter),
                            onDismiss = songBuilderViewModel::onBlockChordEditorDismissed,
                            onChordRemoved = {
                                songBuilderViewModel.onChordRemovedFromBlock(
                                    blockIndex = indexAndChord.blockIndex,
                                    chordIndex = indexAndChord.chordIndex
                                )
                            },
                            chordType = indexAndChord.chordType
                        )
                    }

                    textEditDialogState.value?.let { indexAndText ->
                        TextEditDialog(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .padding(top = 50.dp, start = 20.dp, end = 20.dp)
                                .align(Alignment.TopCenter),
                            currentText = indexAndText.second,
                            onTextEdited = { text ->
                                songBuilderViewModel.onChordsBlockTextChanged(
                                    indexAndText.first,
                                    text
                                )
                            },
                            onDismiss = songBuilderViewModel::onTextEditorDismissed
                        )
                    }

                    chordRemoveConfirmationState.value?.let { index ->
                        ConfirmationDialog(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center),
                            onConfirmed = { songBuilderViewModel.onChordBlockRemoved(index) },
                            onDismiss = { songBuilderViewModel.onConfirmationDismissed() }
                        )
                    }
                }
            }
        }
    }
}
