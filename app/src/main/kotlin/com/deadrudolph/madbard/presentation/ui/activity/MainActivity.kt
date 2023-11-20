package com.deadrudolph.madbard.presentation.ui.activity

import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.deadrudolph.core.base.action.ActivityActions
import com.deadrudolph.feature_builder.presentation.ui.tab.SongBuilderTab
import com.deadrudolph.home.presentation.ui.screen.HomeTab
import com.deadrudolph.madbard.R
import com.deadrudolph.madbard.di.component.main.AppComponentHolder
import com.deadrudolph.madbard.utils.TabNavigationItem
import com.deadrudolph.tuner.view.TunerTab
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme
import com.deadrudolph.uicomponents.utils.composition_locals.LocalContentSize
import javax.inject.Inject

internal class MainActivity : ComponentActivity(), ActivityActions {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel: MainActivityViewModel by viewModels {
        AppComponentHolder.getInternal().getViewModelFactory()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        AppComponentHolder.getInternal().inject(this)

        super.onCreate(savedInstanceState)

        /**
         * This needed to avoid Jetpack Compose unexpected and weird behavior
         * when "adjustResize" mode is set
         * */
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val homeTab = HomeTab()
            val builderTab = SongBuilderTab()
            val tunerTab = TunerTab()

            TabNavigator(homeTab) {
                val contentSizeState = viewModel.contentSizeState.collectAsState()
                val bottomBarState = viewModel.bottomBarVisibilityState.collectAsState()

                CompositionLocalProvider(
                    LocalContentSize provides contentSizeState.value
                ) {
                    DefaultTheme {
                        Scaffold(
                            modifier = Modifier
                                .navigationBarsPadding()
                                .statusBarsPadding(),
                            content = {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(
                                            bottom = it.calculateBottomPadding(),
                                            top = it.calculateTopPadding()
                                        )
                                        .onGloballyPositioned { coords ->
                                            val height =
                                                coords.positionInWindow().y + coords.size.height
                                            val width =
                                                coords.positionInWindow().x + coords.size.width
                                            viewModel.onContentSizeChanged(
                                                Size(width.toInt(), height.toInt())
                                            )
                                        }
                                ) {


                                    CurrentTab()
                                }
                            },
                            bottomBar = {
                                if (bottomBarState.value) {
                                    NavBar(homeTab, builderTab, tunerTab)
                                }
                            }
                        )
                    }
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

    override fun onBottomBarVisible(isVisible: Boolean) {
        viewModel.setBottomBarVisible(isVisible)
    }
}
