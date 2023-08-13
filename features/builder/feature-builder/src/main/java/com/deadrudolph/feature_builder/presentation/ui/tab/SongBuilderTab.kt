package com.deadrudolph.feature_builder.presentation.ui.tab

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.deadrudolph.feature_builder.R
import com.deadrudolph.feature_builder.presentation.ui.screen.song_builder.SongBuilderScreen

class SongBuilderTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.tab_builder)
            val icon = rememberVectorPainter(
                image = ImageVector.vectorResource(id = R.drawable.ic_wrench)
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
        Navigator(SongBuilderScreen())
    }
}