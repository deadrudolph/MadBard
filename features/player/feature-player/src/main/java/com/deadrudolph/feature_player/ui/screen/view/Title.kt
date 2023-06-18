package com.deadrudolph.feature_player.ui.screen.view

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
internal fun Title(text: String, modifier: Modifier) {
    Text(
        modifier = modifier,
        text = text,
        style = CustomTheme.typography.title,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center
    )
}