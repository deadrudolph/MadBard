package com.deadrudolph.feature_builder.presentation.ui.screen.song_import

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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.feature_builder.R
import com.deadrudolph.feature_builder.R.string
import com.deadrudolph.feature_builder.di.component.SongBuilderComponentHolder
import com.deadrudolph.feature_builder.util.keyboard.keyboardOpenState
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme
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

        DefaultTheme {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    modifier = Modifier
                        .wrapContentSize()
                        .align(End)
                        .padding(top = 10.dp, end = 10.dp)
                        .clickable {
                            songImportViewModel.onSongAnalyzeClicked()
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