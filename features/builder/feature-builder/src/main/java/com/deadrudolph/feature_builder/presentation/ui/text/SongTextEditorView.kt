package com.deadrudolph.feature_builder.presentation.ui.text

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.dpToPx
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.feature_builder.presentation.ui.chord.ChordItem
import com.deadrudolph.feature_builder.presentation.ui.model.ChordUIModel
import com.deadrudolph.feature_builder.presentation.ui.model.TextFieldState
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionEnd
import com.deadrudolph.feature_builder.util.keyboard.keyboardOpenState
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.utils.composition_locals.LocalContentSize
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
internal fun SongTextEditorView(
    modifier: Modifier,
    textFieldsState: StateFlow<List<TextFieldState>>,
    onTextStateChanged: (Int, TextFieldValue) -> Unit,
    onTextLayoutResultChanged: (Int, TextLayoutResult) -> Unit,
    onKeyBack: (Int) -> Unit,
    onChordClicked: (Int, ChordUIModel) -> Unit
) {

    val textFieldStates = textFieldsState.collectAsState()
    val keyboardHeightState = keyboardOpenState()
    val view = LocalView.current

    val rect = android.graphics.Rect()
    view.rootView.getWindowVisibleDisplayFrame(rect)

    val currentBottomPadding = calculateBottomPadding(
        contentHeight = LocalContentSize.current.height,
        isKeyboardOpened = keyboardHeightState.value,
        visibleFrameHeight = rect.bottom
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = currentBottomPadding.coerceAtLeast(0.dp))
    ) {
        itemsIndexed(textFieldStates.value) { index, textFieldState ->

            ChordsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 2.dp, bottom = 1.dp),
                chordsList = textFieldState.chordsList,
                onChordClicked = { chord ->
                    onChordClicked(index, chord)
                }
            )

            InputField(
                textFieldState = textFieldState,
                currentBottomPadding = currentBottomPadding,
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
    chordsList: List<ChordUIModel>,
    onChordClicked: (ChordUIModel) -> Unit
) {
    if (chordsList.isEmpty()) return
    Box(
        modifier = modifier
    ) {
        chordsList.forEach { chord ->
            ChordItem(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(start = chord.horizontalOffset.pxToDp.dp)
                    .clickable {
                        onChordClicked(chord)
                    },
                chord = chord
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class)
@Composable
internal fun InputField(
    textFieldState: TextFieldState,
    currentBottomPadding: Dp,
    onTextStateChanged: (TextFieldValue) -> Unit,
    onTextLayoutResultChanged: (TextLayoutResult) -> Unit,
    onKeyBack: () -> Unit
) {

    val focusRequester = remember { FocusRequester() }
    val bringIntoViewRequester = BringIntoViewRequester()
    val coroutineScope = rememberCoroutineScope()

    BasicTextField(
        modifier = Modifier
            .wrapContentHeight()
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .onKeyEvent {
                if (it.key == Key.Backspace) {
                    onKeyBack()
                    true
                } else false
            }
            .bringIntoViewRequester(bringIntoViewRequester)
            .padding(
                all = 5.dp
            ),
        value = textFieldState.value,
        onValueChange = { value ->
            onTextStateChanged(value)

        },
        cursorBrush = SolidColor(Color.White),
        textStyle = CustomTheme.typography.songsBuilder.copy(
            color = Color.White
        ),
        onTextLayout = { textLayoutResult ->
            if (textFieldState.isFocused) {
                val cursorRect = textLayoutResult.getCursorRect(
                    textFieldState.value.getTrueSelectionEnd()
                )
                val offset = (cursorRect.bottom + currentBottomPadding.value.dpToPx) * 1.1f
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView(
                        Rect(Offset(0f, offset), Size.Zero)
                    )
                }
            }
            onTextLayoutResultChanged(textLayoutResult)
        }
    )

    SideEffect {
        if (textFieldState.isFocused) {
            try {
                focusRequester.requestFocus()
            } catch (e: IllegalArgumentException) {
                Timber.e("Error while requesting focus: $e")
            }
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
