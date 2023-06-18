package com.deadrudolph.feature_builder.presentation.ui.chord

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.view.ChordCustomView

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
                Row(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .background(CustomTheme.colors.dark_700_65)
                        .clip(
                            RoundedCornerShape(5.dp)
                        )
                        .padding(horizontal = 24.dp, vertical = 8.dp)
                        .wrapContentHeight()
                        .clickable { onChordSelected(item) }
                ) {

                    ChordCustomView(
                        modifier = Modifier
                            .size(width = 80.dp, height = 100.dp),
                        chordType = item
                    )

                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .wrapContentSize(align = Alignment.Center)
                            .align(Alignment.CenterVertically),
                        text = item.marker,
                        style = CustomTheme.typography.title
                    )
                }
            }
        }
    )
}