package com.deadrudolph.feature_builder.presentation.ui.screen

import android.graphics.drawable.shapes.Shape
import android.text.TextPaint
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalTextToolbar
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextPainter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.android.InternalPlatformTextApi
import androidx.compose.ui.text.android.TextLayout
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
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