package com.deadrudolph.feature_builder.presentation.ui.text

import android.graphics.Rect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.feature_builder.presentation.ui.model.ChordUIModel
import com.deadrudolph.feature_builder.presentation.ui.model.TextFieldState
import com.deadrudolph.feature_builder.util.keyboard.keyboardOpenState
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.utils.composition_locals.LocalContentSize
import com.deadrudolph.uicomponents.utils.logslogs
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun SongTextEditorView(
    modifier: Modifier,
    textFieldsState: StateFlow<List<TextFieldState>>,
    onTextStateChanged: (Int, TextFieldValue) -> Unit,
    onTextLayoutResultChanged: (Int, TextLayoutResult) -> Unit,
    onKeyBack: (Int) -> Unit
) {

    val textFieldStates = textFieldsState.collectAsState()
    val keyboardHeightState = keyboardOpenState()
    val view = LocalView.current

    val rect = Rect()
    view.rootView.getWindowVisibleDisplayFrame(rect)

    val currentBottomPadding = calculateBottomPadding(
        contentHeight = LocalContentSize.current.height,
        isKeyboardOpened = keyboardHeightState.value,
        visibleFrameHeight = rect.bottom
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = currentBottomPadding.coerceAtLeast(0.dp))
    ) {
        itemsIndexed(textFieldStates.value) { index, textFieldState ->

            ChordsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp),
                chordsList = textFieldState.chordsList
            )

            InputField(
                textFieldState = textFieldState,
                onTextStateChanged = { text ->
                    onTextStateChanged(index, text)
                },
                onTextLayoutResultChanged = { layoutResult ->
                    onTextLayoutResultChanged(index, layoutResult)
                },
                onKeyBack = {
                    onKeyBack(index)
                }
            )
        }
    }
}

@Composable
internal fun ChordsRow(
    modifier: Modifier,
    chordsList: List<ChordUIModel>
) {
    Box(
        modifier = modifier
    ) {
        chordsList.forEach { chord ->
            Text(
                modifier = Modifier
                    .padding(
                        start = chord.horizontalOffset.pxToDp.dp,
                        top = 5.dp,
                        bottom = 5.dp
                    )
                    .wrapContentSize()
                    .background(color = Color.Green),
                text = chord.chordType.marker,
                color = Color.White
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun InputField(
    textFieldState: TextFieldState,
    onTextStateChanged: (TextFieldValue) -> Unit,
    onTextLayoutResultChanged: (TextLayoutResult) -> Unit,
    onKeyBack: () -> Unit
) {

    val focusRequester = remember { FocusRequester() }

    BasicTextField(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onKeyEvent {
                if(it.key == Key.Backspace) {
                    onKeyBack()
                    true
                } else false
            }
            .padding(
                top = 20.dp,
                start = 5.dp,
                end = 5.dp
            )
            .background(Color.DarkGray),
        value = textFieldState.value,
        onValueChange = { value ->
            logslogs("onValueChanged")
            onTextStateChanged(value)
        },
        cursorBrush = SolidColor(Color.White),
        textStyle = CustomTheme.typography.songsBuilder.copy(
            color = Color.White
        ),
        onTextLayout = { textLayoutResult ->
            onTextLayoutResultChanged(textLayoutResult)
        }
    )

    SideEffect {
        if (textFieldState.isFocused) {
            focusRequester.requestFocus()
        }
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
