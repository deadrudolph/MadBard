package com.deadrudolph.home.presentation.ui.screen.home.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.hilt.getScreenModel
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.feature_home.R
import com.deadrudolph.home.di.component.HomeComponentHolder
import com.deadrudolph.home.presentation.ui.screen.home.grid_section.DashboardItemTitle
import com.deadrudolph.home.presentation.ui.screen.home.grid_section.SongsGrid
import com.deadrudolph.home.presentation.ui.screen.home.horizontal_list_section.SongsHorizontalList
import com.deadrudolph.home.presentation.ui.screen.home.own_songs_section.OwnSongsSection
import com.deadrudolph.home_domain.domain.model.time_of_day.TimeOfDay
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
        val homeViewModel =
            getDaggerViewModel<HomeViewModel>(
                viewModelProviderFactory = HomeComponentHolder.getInternal().getViewModelFactory()
            )

        //Fetch data
        LaunchedEffect(Unit) {
            homeViewModel.fetchContent()
        }

        ScreenContent(homeViewModel)

    }

    @Composable
    private fun ScreenContent(homeViewModel: HomeViewModel) {
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
                    SongsGrid(homeViewModel)
                }
                item {
                    DashboardItemTitle(
                        modifier = Modifier.padding(start = 20.dp, bottom = 16.dp, top = 50.dp),
                        titleRes = R.string.feature_home_recently_played
                    )
                }
                item {
                    SongsHorizontalList(homeViewModel)
                }
                item {
                    DashboardItemTitle(
                        modifier = Modifier.padding(start = 20.dp, bottom = 16.dp, top = 50.dp),
                        titleRes = R.string.feature_home_own_songs
                    )
                }
                item {
                    OwnSongsSection(homeViewModel = homeViewModel)
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
                    painter = painterResource(id = R.drawable.ic_settings),
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