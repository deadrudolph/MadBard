package com.deadrudolph.home.presentation.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.deadrudolph.feature_home.R
import com.deadrudolph.home.presentation.ui.screen.home.main.HomeScreen

class HomeTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.tab_home)
            val icon = rememberVectorPainter(
                image = ImageVector.vectorResource(id = R.drawable.ic_home)
            )

            return remember {
                TabOptions(
                    index = 0u,
                    title = title,
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(HomeScreen())
    }
}