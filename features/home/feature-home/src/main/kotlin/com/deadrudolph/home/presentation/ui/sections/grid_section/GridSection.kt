package com.deadrudolph.home.presentation.ui.sections.grid_section

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
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
internal fun SongsGrid(
    recommendedSongsStateFlow: StateFlow<Result<List<SongItem>>>,
    onSongItemClicked: (SongItem) -> Unit
) {
    recommendedSongsStateFlow
        .collectAsState()
        .value
        .LoadState(
            onRestartState = {},
            loadingView = { isLoading ->
                if (isLoading) {
                    Loading()
                }
            },
            errorView = {
                Loading()
            }
        ) { songs ->
            songs.chunked(2).forEach { pairOfSongs ->
                pairOfSongs.firstOrNull()?.let { firstItem ->
                    GridItem(
                        modifier = Modifier.padding(top = 8.dp),
                        firstItem = firstItem,
                        secondItem = pairOfSongs.getOrNull(1),
                        onSongItemClicked = onSongItemClicked
                    )
                }
            }
        }
}

@Composable
fun DashboardItemTitle(
    modifier: Modifier,
    titleRes: Int
) {
    Text(
        modifier = modifier.then(
            Modifier
                .wrapContentSize()
        ),
        text = stringResource(id = titleRes),
        style = CustomTheme.typography.title
    )
}

@Composable
fun GridItem(
    modifier: Modifier,
    firstItem: SongItem,
    secondItem: SongItem?,
    onSongItemClicked: (SongItem) -> Unit
) {
    Row(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(0.5f)
                .padding(start = 16.dp, end = 4.dp)
        ) {
            BoxGridItem(
                modifier = Modifier
                    .fillMaxSize(),
                imagePath = firstItem.imagePath,
                title = firstItem.title,
                onSongItemClicked = {
                    onSongItemClicked(firstItem)
                }
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .weight(0.5f)
                .padding(start = 4.dp, end = 16.dp)
        ) {
            secondItem?.let { itemSecond ->
                BoxGridItem(
                    modifier = Modifier
                        .fillMaxSize(),
                    imagePath = itemSecond.imagePath,
                    title = itemSecond.title,
                    onSongItemClicked = {
                        onSongItemClicked(secondItem)
                    }
                )
            }
        }
    }
}

@Composable
fun BoxGridItem(
    modifier: Modifier,
    imagePath: String?,
    title: String,
    onSongItemClicked: () -> Unit
) {
    Row(
        modifier = modifier.then(
            Modifier
                .height(60.dp)
                .clip(RoundedCornerShape(5.dp))
                .background(color = MaterialTheme.colors.surface)
                .clickable {
                    onSongItemClicked()
                }
        )
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imagePath)
                .build(),
            modifier = Modifier
                .fillMaxHeight(),
            error = painterResource(id = drawable.img_song_default),
            contentDescription = "Avatar",
            contentScale = ContentScale.Fit,
            placeholder = painterResource(id = drawable.img_song_default)
        )
        Text(
            modifier = Modifier
                .wrapContentSize()
                .padding(all = 8.dp)
                .align(Alignment.CenterVertically),
            text = title,
            style = CustomTheme.typography.subTitle,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun Loading() {
    val shimmerInstance = rememberShimmer(shimmerBounds = ShimmerBounds.Window)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(top = 8.dp)
            .shimmer(shimmerInstance)
    ) {
        ShimmerBox(
            modifier = Modifier
                .padding(start = 8.dp, end = 4.dp)
                .weight(1f)
        )
        ShimmerBox(
            modifier = Modifier
                .padding(start = 4.dp, end = 8.dp)
                .weight(1f)
        )
    }
}

@Composable
private fun ShimmerBox(
    modifier: Modifier
) {
    Box(
        modifier = modifier.then(
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .background(MaterialTheme.colors.surface)
                .clip(RoundedCornerShape(5.dp))
        )
    )
}