package com.deadrudolph.feature_builder.presentation.ui.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest.Builder
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.uicomponents.R.drawable
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.utils.LoadState
import com.puls.stateutil.Result
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun SongPickerDialog(
    modifier: Modifier,
    onDismiss: () -> Unit,
    onSongSelected: (SongItem) -> Unit,
    songsState: StateFlow<Result<List<SongItem>>>
) {
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
        properties = DialogProperties(
            decorFitsSystemWindows = false
        )
    ) {
        val brush = Brush.linearGradient(
            colors = listOf(CustomTheme.colors.dark_700_65, CustomTheme.colors.dark_800)
        )

        Column(
            modifier = modifier.then(
                Modifier
                    .clip(
                        RoundedCornerShape(10.dp)
                    )
                    .background(brush)
            )
        ) {

            Box(modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
            ) {
                IconButton(
                    onClick = onDismiss,
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

            songsState
                .collectAsState()
                .value
                .LoadState(
                    onRestartState = {},
                ) { songs ->
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        content = {
                            items(items = songs) { item ->
                                Row(
                                    modifier = Modifier
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .clip(
                                            RoundedCornerShape(5.dp)
                                        )
                                        .padding(horizontal = 24.dp, vertical = 8.dp)
                                        .wrapContentHeight()
                                        .clickable { onSongSelected(item) }
                                ) {

                                    AsyncImage(
                                        model = Builder(LocalContext.current)
                                            .data(item.imagePath)
                                            .build(),
                                        modifier = Modifier
                                            .size(width = 80.dp, height = 100.dp),
                                        error = painterResource(id = drawable.img_song_default),
                                        contentDescription = "Avatar",
                                        contentScale = ContentScale.Fit,
                                        placeholder = painterResource(id = drawable.img_song_default)
                                    )

                                    Text(
                                        modifier = Modifier
                                            .padding(start = 20.dp)
                                            .wrapContentSize(align = Alignment.Center)
                                            .align(Alignment.CenterVertically),
                                        text = item.title,
                                        style = CustomTheme.typography.title
                                    )
                                }
                            }
                        }
                    )
                }
        }
    }
}
