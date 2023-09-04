package com.deadrudolph.feature_builder.presentation.ui.chord

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.Modifier.Companion
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_domain.model.ChordGroup
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.uicomponents.R.drawable
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.view.ChordCustomView
import com.deadrudolph.uicomponents.compose.view.ChordItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChordPicker(
    modifier: Modifier = Modifier,
    onChordSelected: (ChordType) -> Unit,
    onDismissDialog: () -> Unit
) {
    val brush = Brush.linearGradient(
        colors = listOf(CustomTheme.colors.dark_700_65, CustomTheme.colors.dark_800),
    )

    val tabIndex = remember { mutableStateOf(0) }

    val chordGroups = ChordGroup.values()

    Column(
        modifier = modifier.then(
            Modifier
                .clip(
                    RoundedCornerShape(10.dp)
                )
                .background(brush)
        )
    ) {
        val currentTabIndex = tabIndex.value

        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
        ) {
            IconButton(
                onClick = onDismissDialog,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .align(Alignment.CenterEnd)
            ) {
                Image(
                    painter = painterResource(id = drawable.ic_cross),
                    contentDescription = "Icon Settings"
                )
            }
        }

        FlowRow(
            modifier = Modifier
                .wrapContentSize()
                .padding(horizontal = 8.dp),
        ) {
            chordGroups.forEachIndexed { index, chordGroup ->
                ChordItem(
                    modifier = Modifier
                        .padding(10.dp)
                        .wrapContentWidth()
                        .height(30.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (currentTabIndex == index) CustomTheme.colors.dark_600
                            else Color.White
                        )
                        .clickable { tabIndex.value = index },
                    chordName = chordGroup.marker
                )
            }
        }

        LazyColumn(
            modifier = Modifier.wrapContentSize(),
            content = {
                items(
                    items = ChordType.values()
                        .filter { it.chordGroup == chordGroups.getOrNull(currentTabIndex) }) { item ->
                    Row(
                        modifier = Modifier
                            .wrapContentHeight()
                            .fillMaxWidth()
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
}
