package com.deadrudolph.madbard.presentation.ui.activity

import android.util.Size
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

internal class MainActivityViewModelImpl @Inject constructor() : MainActivityViewModel() {

    override val contentSizeState = MutableStateFlow(Size(0, 0))

    override val bottomBarVisibilityState = MutableStateFlow(true)

    override fun onContentSizeChanged(size: Size) {
        contentSizeState.value = size
    }

    override fun setBottomBarVisible(isVisible: Boolean) {
        bottomBarVisibilityState.value = isVisible
    }
}
