package com.deadrudolph.feature_builder.presentation.ui.chord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
fun ChordPicker(
    modifier: Modifier = Modifier,
    onChordSelected: (ChordType) -> Unit
) {
    val brush = Brush.radialGradient(
        colors = listOf(CustomTheme.colors.dark_700_65, CustomTheme.colors.dark_800),
        radius = 300f,
        center = Offset.Zero
    )

    LazyColumn(
        modifier = modifier.then(
            Modifier
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(brush)
        ),
        content = {
            items(items = ChordType.values()) { item ->
                Box(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(CustomTheme.colors.dark_700_65)
                        .clip(
                            RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .height(20.dp)
                        .clickable { onChordSelected(item) }
                ) {
                    Text(
                        modifier = Modifier
                            .wrapContentSize(align = Alignment.Center),
                        text = item.marker
                    )
                }
            }
        }
    )
}