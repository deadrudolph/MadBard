package com.deadrudolph.uicomponents.compose.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
fun PlayerTextBlock(
    modifier: Modifier,
    text: String,
    style: TextStyle? = null,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null
) {
    Text(
        modifier = modifier,
        text = text,
        style = style ?: CustomTheme.typography.songsBuilder.copy(
            color = Color.White
        ),
        onTextLayout = { result ->
            onTextLayout?.invoke(result)
        }
    )
}