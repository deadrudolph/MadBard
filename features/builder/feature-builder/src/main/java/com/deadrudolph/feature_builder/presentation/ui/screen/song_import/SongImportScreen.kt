package com.deadrudolph.feature_builder.presentation.ui.screen.song_import

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.End
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.feature_builder.R
import com.deadrudolph.feature_builder.R.string
import com.deadrudolph.feature_builder.di.component.SongBuilderComponentHolder
import com.deadrudolph.feature_builder.presentation.ui.screen.song_builder.SongBuilderViewModel
import com.deadrudolph.feature_builder.presentation.ui.view.LoadingDialog
import com.deadrudolph.feature_builder.ui_model.ToastType.NO_CHORDS
import com.deadrudolph.feature_builder.ui_model.ToastType.NO_TEXT
import com.deadrudolph.feature_builder.util.keyboard.keyboardOpenState
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme
import com.deadrudolph.uicomponents.utils.LoadState
import com.deadrudolph.uicomponents.utils.composition_locals.LocalContentSize
import kotlinx.coroutines.flow.StateFlow

internal class SongImportScreen : AndroidScreen() {

    @Composable
    override fun Content() {
        val songImportViewModel =
            getDaggerViewModel<SongImportViewModel>(
                viewModelProviderFactory = SongBuilderComponentHolder.getInternal()
                    .getViewModelFactory()
            )
        val songBuilderViewModel = getDaggerViewModel<SongBuilderViewModel>(
            viewModelProviderFactory = SongBuilderComponentHolder.getInternal()
                .getViewModelFactory(),
            isSharedViewModel = true
        )

        val context = LocalContext.current

        songImportViewModel
            .analyzedSongState
            .collectAsState()
            .value
            .LoadState(
                onRestartState = { },
                enableLoading = true,
                loadingView = { isLoading ->
                    if (isLoading) LoadingDialog()
                },
                successContent = { songItem ->
                    val navigator = LocalNavigator.currentOrThrow
                    songBuilderViewModel.setSong(songItem)
                    navigator.pop()
                }
            )

        songImportViewModel.toastMessageState.collectAsState().value?.let {
            Toast.makeText(
                context,
                when (it) {
                    NO_TEXT -> R.string.error_no_text
                    NO_CHORDS -> R.string.error_no_chords
                },
                Toast.LENGTH_LONG
            )
        }

        DefaultTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(End)
                        .padding(top = 10.dp, end = 10.dp)
                        .clickable {
                            songImportViewModel.analyzeSong()
                        },
                    text = stringResource(id = string.button_analyze),
                    style = CustomTheme.typography.subTitle.copy(
                        color = Color.White
                    ),
                    textDecoration = TextDecoration.Underline
                )
                TextBlock(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(all = 5.dp),
                    textFieldState = songImportViewModel.textFieldState,
                    onTextLayoutResultChanged = songImportViewModel::onTextLayoutResultChanged,
                    onTextValueChanged = songImportViewModel::onTextValueChanged
                )
            }
        }
    }

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    private fun TextBlock(
        modifier: Modifier,
        textFieldState: StateFlow<TextFieldValue>,
        onTextLayoutResultChanged: (TextLayoutResult) -> Unit,
        onTextValueChanged: (TextFieldValue) -> Unit
    ) {

        val keyboardHeightState = keyboardOpenState()
        val view = LocalView.current

        val rect = android.graphics.Rect()
        view.rootView.getWindowVisibleDisplayFrame(rect)

        val currentBottomPadding = calculateBottomPadding(
            contentHeight = LocalContentSize.current.height,
            isKeyboardOpened = keyboardHeightState.value,
            visibleFrameHeight = rect.bottom
        )

        val textFieldValue = textFieldState.collectAsState()

        BasicTextField(
            value = textFieldValue.value,
            modifier = modifier,
            textStyle = CustomTheme.typography.songsBuilder.copy(
                color = Color.White
            ),
            onTextLayout = { result ->
                onTextLayoutResultChanged(result)
            },
            onValueChange = { value ->
                onTextValueChanged(value)
            },
            cursorBrush = SolidColor(Color.White),
            decorationBox = @Composable { innerTextField ->
                TextFieldDefaults.TextFieldDecorationBox(
                    value = textFieldValue.value.text,
                    innerTextField = innerTextField,
                    placeholder = @Composable {
                        Text(
                            modifier = Modifier.wrapContentSize(),
                            text = stringResource(id = R.string.paste_here)
                        )
                    },
                    enabled = true,
                    interactionSource = remember { MutableInteractionSource() },
                    singleLine = false,
                    visualTransformation = VisualTransformation.None,
                    contentPadding = PaddingValues(bottom = currentBottomPadding)
                )
            }
        )
    }
}

private fun calculateBottomPadding(
    isKeyboardOpened: Boolean,
    contentHeight: Int,
    visibleFrameHeight: Int
): Dp {
    return if (!isKeyboardOpened) 5.dp
    else {
        (contentHeight - visibleFrameHeight).pxToDp.dp + 5.dp
    }
}
