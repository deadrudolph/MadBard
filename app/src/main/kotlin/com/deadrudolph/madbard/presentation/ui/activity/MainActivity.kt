package com.deadrudolph.madbard.presentation.ui.activity

import android.content.res.Resources
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.deadrudolph.madbard.R
import com.deadrudolph.madbard.di.component.main.AppComponentHolder
import com.deadrudolph.madbard.utils.TabNavigationItem
import com.deadrudolph.core.base.action.ActivityActions
import com.deadrudolph.home.presentation.ui.screen.HomeTab
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme

internal class MainActivity : ComponentActivity(), ActivityActions {

    override fun onCreate(savedInstanceState: Bundle?) {
        AppComponentHolder.getInternal().inject(this)
        super.onCreate(savedInstanceState)

        setContent {
            val homeTab = HomeTab()

            TabNavigator(homeTab) {
                DefaultTheme {
                    Scaffold(
                        content = {
                            CurrentTab()
                        },
                        bottomBar = {
                            NavBar(homeTab)
                        }
                    )
                }
            }
        }
    }


    override fun getTheme(): Resources.Theme {
        val theme = super.getTheme()
        theme.applyStyle(R.style.Theme_ComposeMultiModuleTemplate, true)
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