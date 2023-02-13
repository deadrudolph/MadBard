package com.deadrudolph.madbard.utils

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.deadrudolph.uicomponents.compose.theme.CustomTheme

@Composable
internal fun RowScope.TabNavigationItem(tab: Tab) {
    val tabNavigator = LocalTabNavigator.current

    BottomNavigationItem(
        selected = tabNavigator.current == tab,
        onClick = { tabNavigator.current = tab },
        icon = {
            Icon(
                painter = tab.options.icon ?: return@BottomNavigationItem,
                contentDescription = tab.options.title
            )
        },
        label = {
            Text(
                text = tab.options.title,
                style = CustomTheme.typography.caption2
            )
        },
        selectedContentColor = CustomTheme.colors.white_300,
        unselectedContentColor = CustomTheme.colors.dark_600,
        alwaysShowLabel = true,
    )
}