package com.deadrudolph.feature_builder.presentation.ui.text

import android.graphics.Rect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_utils.extension.pxToDp
import com.deadrudolph.feature_builder.util.keyboard.keyboardHeightState
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.utils.composition_locals.LocalContentSize
import com.deadrudolph.uicomponents.utils.logslogs
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun CoreSongTextEditor(
    modifier: Modifier,
    textFieldsState: StateFlow<List<TextFieldValue>>,
    currentFocusIndex: StateFlow<Int>,
    onTextStateChanged: (Int, TextFieldValue) -> Unit,
    onTextLayoutResultChanged: (Int, TextLayoutResult) -> Unit
) {

    val textFields = textFieldsState.collectAsState()
    val currentFocusIndexState = currentFocusIndex.collectAsState()
    val focusRequester = remember { FocusRequester() }
    val keyboardHeightState = keyboardHeightState()
    val view = LocalView.current

    val rect = Rect()
    view.rootView.getWindowVisibleDisplayFrame(rect)

    val currentBottomPadding = calculateBottomPadding(
        contentHeight = LocalContentSize.current.height,
        isKeyboardOpened = keyboardHeightState.value.isOpened,
        visibleFrameHeight = rect.bottom
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = currentBottomPadding.coerceAtLeast(0.dp))
    ) {
        itemsIndexed(textFields.value) { index, textFieldValue ->
            BasicTextField(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
                    .padding(
                        top = 20.dp,
                        start = 5.dp,
                        end = 5.dp
                    )
                    .background(Color.DarkGray),
                value = textFieldValue,
                onValueChange = { value ->
                    onTextStateChanged(index, value)
                },
                cursorBrush = SolidColor(Color.White),
                textStyle = CustomTheme.typography.songsBuilder.copy(
                    color = Color.White
                ),
                onTextLayout = { textLayoutResult ->
                    onTextLayoutResultChanged(index, textLayoutResult)
                }
            )

            LaunchedEffect(Unit) {
                if (currentFocusIndexState.value == index) {
                    focusRequester.requestFocus()
                }
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
