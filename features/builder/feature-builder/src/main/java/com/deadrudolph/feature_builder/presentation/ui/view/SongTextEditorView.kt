package com.deadrudolph.feature_builder.presentation.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.deadrudolph.feature_builder.ui_model.ChordItemBlockModel
import com.deadrudolph.feature_builder.util.extension.getTrueSelectionEnd
import com.deadrudolph.feature_builder.util.keyboard.keyboardOpenState
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.view.ChordsBlock
import com.deadrudolph.uicomponents.compose.view.ChordsRow
import com.deadrudolph.uicomponents.ui_model.ChordUIModel
import com.deadrudolph.uicomponents.ui_model.SongState
import com.deadrudolph.uicomponents.ui_model.TextFieldState
import com.deadrudolph.uicomponents.utils.composition_locals.LocalContentSize
import com.deadrudolph.uicomponents.utils.logslogs
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
internal fun SongTextEditorView(
    modifier: Modifier,
    songState: StateFlow<SongState>,
    onTextStateChanged: (Int, TextFieldValue) -> Unit,
    onTextLayoutResultChanged: (Int, TextLayoutResult) -> Unit,
    onKeyBack: (Int) -> Unit,
    onCommonChordClicked: (Int, ChordUIModel) -> Unit,
    onChordsBlockChordClicked: (chord: ChordItemBlockModel) -> Unit,
    onChordBlockTextClicked: (index: Int, text: String) -> Unit,
    onChordBlockRemoveClicked: (index: Int) -> Unit,
    onChordBlockAddChordClicked: (index: Int) -> Unit
) {

    val textFieldStates = songState.collectAsState()
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
        itemsIndexed(textFieldStates.value.textFields) { index, textFieldState ->
            textFieldState.chordBlock?.let {
                logslogs("Index: $index TextFieldsState: $textFieldState")
            }
            ChordsBlock(
                chordsBlock = textFieldState.chordBlock,
                isEditable = true,
                onChordClicked = { chordType, chordIndex ->
                    onChordsBlockChordClicked(
                        ChordItemBlockModel(
                            blockIndex = index,
                            chordIndex = chordIndex,
                            chordType = chordType
                        )
                    )
                },
                onChordBlockTextClicked = { text ->
                    onChordBlockTextClicked(index, text)
                },
                onBlockRemoveClicked = {
                    onChordBlockRemoveClicked(index)
                },
                onAddChordClicked = {
                    onChordBlockAddChordClicked(index)
                }
            )

            ChordsRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(top = 2.dp, bottom = 1.dp),
                chordsList = textFieldState.chordsList,
                onChordClicked = { chord ->
                    onCommonChordClicked(index, chord)
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
