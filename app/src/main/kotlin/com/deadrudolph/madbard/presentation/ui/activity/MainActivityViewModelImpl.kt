package com.deadrudolph.madbard.presentation.ui.activity

import android.util.Size
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow

internal class MainActivityViewModelImpl @Inject constructor(): MainActivityViewModel() {

    override val contentSizeState = MutableStateFlow<Size>(Size(0,0))

    override fun onContentSizeChanged(size: Size) {
        contentSizeState.value = size
    }

}