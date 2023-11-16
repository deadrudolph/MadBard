package com.deadrudolph.tuner.view

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.deadrudolph.tuner.R
import com.deadrudolph.tuner.view.tuner.TunerScreen

class TunerTab : Tab {

    override val options: TabOptions
        @Composable
        get() {
            val title = stringResource(R.string.tuner_tab)
            val icon = rememberVectorPainter(
                image = ImageVector.vectorResource(id = R.drawable.ic_tuner)
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
        Navigator(TunerScreen())
    }
}
