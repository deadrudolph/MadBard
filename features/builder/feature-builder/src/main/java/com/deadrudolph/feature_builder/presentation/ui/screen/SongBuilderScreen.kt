package com.deadrudolph.feature_builder.presentation.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.feature_builder.di.component.SongBuilderComponentHolder
import com.deadrudolph.feature_builder.presentation.tool_bar.AndroidTextToolbar
import com.deadrudolph.feature_builder.presentation.ui.chord.ChordPicker
import com.deadrudolph.feature_builder.presentation.ui.text.SongTextEditor
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme
import com.deadrudolph.uicomponents.view.textfield.NormalTextField

internal class SongBuilderScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val homeViewModel =
            getDaggerViewModel<SongBuilderViewModel>(
                viewModelProviderFactory = SongBuilderComponentHolder.getInternal()
                    .getViewModelFactory()
            )

        DefaultTheme {
            /*Box(modifier = Modifier.fillMaxSize()) {
                Screen(homeViewModel)
            }*/
            val textValue = remember { mutableStateOf(TextFieldValue()) }
            Box(modifier = Modifier.fillMaxSize()) {
                NormalTextField(
                    modifier = Modifier.fillMaxSize(),
                    value = textValue.value,
                    onValueChange = {
                        textValue.value = it
                    },
                    textStyle = CustomTheme.typography.songsBuilder,
                    cursorBrush = SolidColor(Color.White)
                )
            }
        }
    }

    @Composable
    private fun Screen(homeViewModel: SongBuilderViewModel) {

        CompositionLocalProvider(
            LocalTextToolbar provides AndroidTextToolbar(LocalView.current) {
                homeViewModel.onNewChord()
            }
        ) {

            Box(modifier = Modifier.fillMaxSize()) {
                val chordsPickerState = homeViewModel.chordPickerStateFlow.collectAsState()

                SongTextEditor(
                    modifier = Modifier.fillMaxSize(),
                    homeViewModel = homeViewModel
                )

                if (chordsPickerState.value) {
                    ChordPicker(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
                            .padding(horizontal = 40.dp, vertical = 20.dp)
                            .align(Alignment.Center),
                        onChordSelected = homeViewModel::onChordSelected
                    )
                }
            }
        }
    }
}