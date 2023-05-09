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
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.nativeKeyCode
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import cafe.adriel.voyager.androidx.AndroidScreen
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.feature_builder.R
import com.deadrudolph.feature_builder.di.component.SongBuilderComponentHolder
import com.deadrudolph.feature_builder.presentation.ui.chord.ChordPicker
import com.deadrudolph.feature_builder.presentation.ui.text.SongTextEditorView
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme
import com.deadrudolph.uicomponents.utils.logslogs

internal class SongBuilderScreen : AndroidScreen() {

    @OptIn(ExperimentalComposeUiApi::class)
    @Composable
    override fun Content() {
        val songBuilderViewModel =
            getDaggerViewModel<SongBuilderViewModel>(
                viewModelProviderFactory = SongBuilderComponentHolder.getInternal()
                    .getViewModelFactory()
            )

        DefaultTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                val chordsPickerState = songBuilderViewModel.chordPickerStateFlow.collectAsState()
                IconButton(
                    modifier = Modifier
                        .padding(10.dp)
                        .size(50.dp, 50.dp)
                        .background(Color.Transparent)
                        .align(Alignment.End),
                    onClick = {
                        songBuilderViewModel.onNewChord()
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_chord),
                        contentDescription = null,
                    )
                }

                Box(modifier = Modifier
                    .fillMaxSize()
                ) {

                    SongTextEditorView(
                        modifier = Modifier.fillMaxSize(),
                        onTextStateChanged = { index, value ->
                            songBuilderViewModel.onTextChanged(index, value)
                        },
                        textFieldsState = songBuilderViewModel.textFieldsStateFlow,
                        onTextLayoutResultChanged = { index, value ->
                            songBuilderViewModel.onLayoutResultChanged(value, index)
                        },
                        onKeyBack = songBuilderViewModel::onTextFieldKeyBack
                    )

                    if (chordsPickerState.value) {
                        Dialog(
                            onDismissRequest = {
                                songBuilderViewModel.onChordSelectionCancelled()
                            }
                        ) {
                            ChordPicker(
                                modifier = Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth()
                                    .padding(horizontal = 40.dp, vertical = 20.dp)
                                    .align(Alignment.Center),
                                onChordSelected = songBuilderViewModel::onChordSelected
                            )
                        }

                    }
                }
            }
        }
    }
}