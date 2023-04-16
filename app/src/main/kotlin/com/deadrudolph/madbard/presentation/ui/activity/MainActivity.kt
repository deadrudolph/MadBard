package com.deadrudolph.madbard.presentation.ui.activity

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.deadrudolph.core.base.action.ActivityActions
import com.deadrudolph.feature_builder.presentation.ui.tab.SongBuilderTab
import com.deadrudolph.home.presentation.ui.screen.HomeTab
import com.deadrudolph.madbard.R
import com.deadrudolph.madbard.di.component.main.AppComponentHolder
import com.deadrudolph.madbard.utils.TabNavigationItem
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme

internal class MainActivity : ComponentActivity(), ActivityActions {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {

        AppComponentHolder.getInternal().inject(this)

        super.onCreate(savedInstanceState)

        setContent {
            val homeTab = HomeTab()
            val builderTab = SongBuilderTab()

            TabNavigator(homeTab) {
                DefaultTheme {
                    Scaffold(
                        content = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(
                                        bottom = it.calculateBottomPadding()
                                    )
                            ) {
                                CurrentTab()
                            }
                        },
                        bottomBar = {
                            NavBar(homeTab, builderTab)
                        }
                    )
                }
            }
        }
    }

    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(R.style.Theme_MadBard, true)
        return theme
    }

    @Composable
    private fun NavBar(vararg tabs: Tab) {
        BottomNavigation(
            backgroundColor = CustomTheme.colors.dark_900_bg
        ) {
            tabs.forEach { tab ->
                TabNavigationItem(tab)
            }
        }
    }
}