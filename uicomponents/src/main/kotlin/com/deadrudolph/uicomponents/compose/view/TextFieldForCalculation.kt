package com.deadrudolph.uicomponents.compose.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
fun TextFieldForCalculation(
    songItem: SongItem,
    onTextLayoutResult: (TextLayoutResult) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        PlayerTextBlock(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(all = 5.dp),
            text = songItem.text,
            style = CustomTheme.typography.songsBuilder.copy(
                color = Color.Transparent
            ),
            onTextLayout = { textLayoutResult ->
                onTextLayoutResult(textLayoutResult)
            }
        )
    }
}