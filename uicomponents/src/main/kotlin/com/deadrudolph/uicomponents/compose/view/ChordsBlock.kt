package com.deadrudolph.uicomponents.compose.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.uicomponents.R
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.ui_model.ChordBlockUIModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChordsBlock(
    chordsBlock: ChordBlockUIModel?,
    isEditable: Boolean = false,
    onChordClicked: (ChordType, index: Int) -> Unit,
    onChordBlockTextClicked: ((String) -> Unit)? = null,
    onBlockRemoveClicked: (() -> Unit)? = null,
    onAddChordClicked: (() -> Unit)? = null
) {
    chordsBlock?.let { chordBlock ->
        Column(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth()
                .padding(bottom = 5.dp)
        ) {
            if (isEditable) {
                Row(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .size(20.dp, 20.dp)
                            .background(Color.Transparent)
                            .align(CenterVertically),
                        onClick = {
                            onBlockRemoveClicked?.invoke()
                        }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_trash),
                            contentDescription = null,
                        )
                    }
                    IconButton(
                        modifier = Modifier
                            .padding(start = 60.dp)
                            .size(30.dp, 30.dp)
                            .background(Color.Transparent)
                            .align(CenterVertically),
                        onClick = {
                            onAddChordClicked?.invoke()
                        }
                    ) {
                        Image(
                            modifier = Modifier.fillMaxSize(),
                            painter = painterResource(id = R.drawable.ic_chord),
                            contentDescription = null,
                        )
                    }

                }
            }
            FlowRow(
                modifier = Modifier
                    .wrapContentSize()
                    .border(
                        width = 1.dp,
                        brush = SolidColor(CustomTheme.colors.dark_600),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 10.dp)
            ) {
                Text(
                    modifier = Modifier
                        .align(CenterVertically)
                        .padding(all = 5.dp)
                        .defaultMinSize(minWidth = 60.dp)
                        .wrapContentSize()
                        .clickable {
                            onChordBlockTextClicked?.invoke(chordBlock.title)
                        },
                    text = chordBlock.title,
                    style = CustomTheme.typography.subTitle.copy(
                        color = Color.White
                    )
                )

                chordBlock.chordsList.forEachIndexed { index, chord ->
                    ChordItem(
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(all = 5.dp)
                            .clickable {
                                onChordClicked(chord, index)
                            },
                        chordName = chord.marker
                    )
                }
            }
        }
    }
}
