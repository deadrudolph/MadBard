package com.deadrudolph.madbard.presentation.ui.activity

import android.util.Size
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

internal abstract class MainActivityViewModel : ViewModel() {

    abstract val contentSizeState: StateFlow<Size>

    abstract val bottomBarVisibilityState: StateFlow<Boolean>

    abstract fun onContentSizeChanged(size: Size)

    abstract fun setBottomBarVisible(isVisible: Boolean)
}
