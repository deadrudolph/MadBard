package com.deadrudolph.feature_builder.presentation.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.deadrudolph.feature_builder.R
import com.deadrudolph.feature_builder.R.drawable
import com.deadrudolph.feature_builder.presentation.ui.screen.song_import.SongImportScreen
import com.deadrudolph.uicomponents.R.drawable.ic_chord

@Composable
internal fun SongBuilderControls(
    modifier: Modifier,
    onNewChord: () -> Unit,
    onNewBlock: (title: String) -> Unit,
    onSaveSong: () -> Unit,
    onAddSong: () -> Unit
) {

    val navigator = LocalNavigator.currentOrThrow
    val context = LocalContext.current

    Box(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterEnd)
        ) {
            IconButton(
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp, 30.dp)
                    .background(Color.Transparent),
                onClick = {
                    onNewBlock(context.getString(R.string.chord_block_default_title))
                }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = drawable.ic_add_block),
                    contentDescription = null,
                )
            }

            IconButton(
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp, 30.dp)
                    .background(Color.Transparent),
                onClick = {
                    onNewChord()
                }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = ic_chord),
                    contentDescription = null,
                )
            }
        }

        Row(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterStart)
        ) {
            IconButton(
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp, 30.dp)
                    .background(Color.Transparent),
                onClick = {
                    onSaveSong()
                }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = drawable.ic_save),
                    contentDescription = null
                )
            }

            IconButton(
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp, 30.dp)
                    .background(Color.Transparent),
                onClick = {
                    onAddSong()
                }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = drawable.ic_add),
                    contentDescription = null
                )
            }

            IconButton(
                modifier = Modifier
                    .padding(10.dp)
                    .size(30.dp, 30.dp)
                    .background(Color.Transparent),
                onClick = {
                    navigator.push(SongImportScreen())
                }
            ) {
                Image(
                    modifier = Modifier.fillMaxSize(),
                    painter = painterResource(id = drawable.ic_import),
                    contentDescription = null
                )
            }
        }
    }
}