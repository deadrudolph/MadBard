package com.deadrudolph.feature_builder.presentation.ui.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.feature_builder.R
import com.deadrudolph.feature_builder.di.component.SongBuilderComponentHolder
import com.deadrudolph.feature_builder.presentation.ui.dialog.ChordEditDialog
import com.deadrudolph.feature_builder.presentation.ui.dialog.ChordsListDialog
import com.deadrudolph.feature_builder.presentation.ui.text.SongTextEditorView
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme

internal class SongBuilderScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val songBuilderViewModel =
            getDaggerViewModel<SongBuilderViewModel>(
                viewModelProviderFactory = SongBuilderComponentHolder.getInternal()
                    .getViewModelFactory()
            )

        DefaultTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                val chordsPickerState = songBuilderViewModel
                    .chordPickerStateFlow.collectAsState()
                val chordEditState = songBuilderViewModel
                    .chordEditDialogStateFlow.collectAsState()

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {

                    IconButton(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(40.dp, 40.dp)
                            .background(Color.Transparent)
                            .align(Alignment.CenterEnd),
                        onClick = {
                            songBuilderViewModel.onNewChord()
                        }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_chord),
                            contentDescription = null,
                        )
                    }

                    IconButton(
                        modifier = Modifier
                            .padding(10.dp)
                            .size(30.dp, 30.dp)
                            .background(Color.Transparent)
                            .align(Alignment.CenterStart),
                        onClick = {
                            songBuilderViewModel.onSaveSongClicked()
                        }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_save),
                            contentDescription = null
                        )
                    }

                }

                Box(modifier = Modifier.fillMaxSize()) {

                    SongTextEditorView(
                        modifier = Modifier.fillMaxSize(),
                        onTextStateChanged = { index, value ->
                            songBuilderViewModel.onTextChanged(index, value)
                        },
                        textFieldsState = songBuilderViewModel.textFieldsStateFlow,
                        onTextLayoutResultChanged = { index, value ->
                            songBuilderViewModel.onLayoutResultChanged(value, index)
                        },
                        onKeyBack = songBuilderViewModel::onTextFieldKeyBack,
                        onChordClicked = songBuilderViewModel::onChordClicked,
                    )

                    if (chordsPickerState.value) {
                        ChordsListDialog(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(horizontal = 40.dp, vertical = 20.dp)
                                .align(Alignment.Center),
                            onDismiss = songBuilderViewModel::onChordSelectionCancelled,
                            onChordSelected = songBuilderViewModel::onChordSelected
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
                            chord = indexAndChord.second
                        )
                    }
                }
            }
        }
    }
}