package com.deadrudolph.home.presentation.ui.screen.home.own_songs_section

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
import com.deadrudolph.feature_home.R
import com.deadrudolph.home.presentation.ui.screen.home.main.HomeViewModel
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.utils.LoadState
import com.valentinilk.shimmer.ShimmerBounds
import com.valentinilk.shimmer.rememberShimmer
import com.valentinilk.shimmer.shimmer

@Composable
internal fun OwnSongsSection(homeViewModel: HomeViewModel) {
    homeViewModel
        .ownSongsStateFlow
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
                    OwnSongItem(
                        imageRes = 1,
                        title = item.title
                    )
                }
            }
        }
}

@Composable
fun OwnSongItem(
    imageRes: Int,
    title: String
) {
    Column(
        modifier = Modifier
            .size(width = 166.dp, height = 200.dp)
            .padding(horizontal = 8.dp)
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {

                },
            painter = painterResource(id = R.drawable.img_song_default),
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
                    .size(width = 166.dp, height = 200.dp)
                    .padding(horizontal = 8.dp)
                    .background(MaterialTheme.colors.surface)
            )
        }
    }
}