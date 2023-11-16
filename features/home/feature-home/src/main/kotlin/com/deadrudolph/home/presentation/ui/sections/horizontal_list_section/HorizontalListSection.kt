package com.deadrudolph.home.presentation.ui.sections.horizontal_list_section

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.uicomponents.R.drawable
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.utils.LoadState
import com.puls.stateutil.Result
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun SongsHorizontalList(
    recentSongsStateFlow: StateFlow<Result<List<SongItem>>>,
    onSongItemClicked: (SongItem) -> Unit
) {
    recentSongsStateFlow
        .collectAsState()
        .value
        .LoadState(
            onRestartState = {},
            loadingView = {
                Loading()
            },
            errorView = {
                Loading()
            }
        ) { songs ->
            LazyRow(
                modifier = Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
                    .wrapContentHeight(),
            ) {
                items(items = songs) { item ->
                    HorizontalListItem(
                        imageRes = 1,
                        title = item.title,
                        onSongItemClicked = {
                            onSongItemClicked(item)
                        }
                    )
                }
            }
        }
}

@Composable
fun HorizontalListItem(
    imageRes: Int,
    title: String,
    onSongItemClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .size(width = 120.dp, height = 145.dp)
            .padding(horizontal = 8.dp)
            .clickable {
                onSongItemClicked()
            }
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth(),
            painter = painterResource(id = drawable.img_song_default),
            contentDescription = "Avatar",
            contentScale = ContentScale.Crop
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(top = 6.dp),
            text = title,
            style = CustomTheme.typography.subTitle,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Loading() {

    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)

    LazyRow(
        modifier = Modifier
            .padding(all = 8.dp)
            .fillMaxWidth()
            .wrapContentHeight()
            .shimmer(shimmerInstance),
    ) {
        items(7) {
            Box(
                modifier = Modifier
                    .size(width = 120.dp, height = 145.dp)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colors.surface)
            )
        }
    }
}