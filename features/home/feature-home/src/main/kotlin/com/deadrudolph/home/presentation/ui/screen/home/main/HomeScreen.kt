package com.deadrudolph.home.presentation.ui.screen.home.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.core.registry.ScreenRegistry
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.core.base.action.ActivityActions
import com.deadrudolph.feature_home.R
import com.deadrudolph.feature_home.R.string
import com.deadrudolph.home.di.component.HomeComponentHolder
import com.deadrudolph.home.presentation.ui.sections.grid_section.DashboardItemTitle
import com.deadrudolph.home.presentation.ui.sections.grid_section.SongsGrid
import com.deadrudolph.home.presentation.ui.sections.horizontal_list_section.SongsHorizontalList
import com.deadrudolph.home.presentation.ui.sections.own_songs_section.OwnSongsSection
import com.deadrudolph.home_domain.domain.model.time_of_day.TimeOfDay
import com.deadrudolph.navigation.screen.SharedScreen.PlayerScreen
import com.deadrudolph.uicomponents.R.drawable
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme

internal class HomeScreen : AndroidScreen() {

    @Composable
    override fun Content() {

        DefaultTheme() {
            Screen()
        }
    }

    @Composable
    private fun Screen() {
        (LocalContext.current as? ActivityActions)?.onBottomBarVisible(true)
        val homeViewModel =
            getDaggerViewModel<HomeViewModel>(
                viewModelProviderFactory = HomeComponentHolder.getInternal().getViewModelFactory()
            )

        val navigator = LocalNavigator.currentOrThrow

        ScreenContent(
            homeViewModel
        ) { songItem ->
            navigator.push(
                ScreenRegistry.get(
                    PlayerScreen(songItem.id)
                )
            )
        }
    }

    @Composable
    private fun ScreenContent(
        homeViewModel: HomeViewModel,
        onSongItemClicked: (SongItem) -> Unit
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            content = {
                item {
                    Settings()
                }
                item {
                    DashboardItemTitle(
                        modifier = Modifier.padding(start = 20.dp, bottom = 16.dp),
                        titleRes = getCurrentTimeOfDayTitleRes(homeViewModel.getCurrentTimeOfDay())
                    )
                }
                item {
                    SongsGrid(
                        recommendedSongsStateFlow = homeViewModel.recommendedSongsStateFlow,
                        onSongItemClicked = onSongItemClicked
                    )
                }
                item {
                    DashboardItemTitle(
                        modifier = Modifier.padding(start = 20.dp, bottom = 16.dp, top = 50.dp),
                        titleRes = string.feature_home_recently_played
                    )
                }
                item {
                    SongsHorizontalList(
                        recentSongsStateFlow = homeViewModel.recentSongsStateFlow,
                        onSongItemClicked = onSongItemClicked
                    )
                }
                item {
                    DashboardItemTitle(
                        modifier = Modifier.padding(start = 20.dp, bottom = 16.dp, top = 50.dp),
                        titleRes = string.feature_home_own_songs
                    )
                }
                item {
                    OwnSongsSection(
                        ownSongsStateFlow = homeViewModel.ownSongsStateFlow,
                        onSongItemClicked = onSongItemClicked
                    )
                }
            })
    }

    @Composable
    private fun Settings() {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(all = 20.dp),
                onClick = {
                    //TODO: Implement later
                }
            ) {
                Image(
                    painter = painterResource(id = drawable.ic_settings),
                    contentDescription = "Icon Settings"
                )
            }
        }
    }

    private fun getCurrentTimeOfDayTitleRes(timeOfDay: TimeOfDay): Int {
        return when (timeOfDay) {
            TimeOfDay.MORNING -> R.string.feature_home_greeting_morning
            TimeOfDay.DAY -> R.string.feature_home_greeting_day
            TimeOfDay.EVENING -> R.string.feature_home_greeting_evening
            TimeOfDay.AFTERNOON -> R.string.feature_home_greeting_afternoon
        }
    }
}
