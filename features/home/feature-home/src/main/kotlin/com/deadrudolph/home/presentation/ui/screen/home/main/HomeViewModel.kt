package com.deadrudolph.home.presentation.ui.screen.home.main

import android.content.Context
import androidx.lifecycle.ViewModel
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.home_domain.domain.model.time_of_day.TimeOfDay
import com.deadrudolph.common_domain.model.Result
import kotlinx.coroutines.flow.StateFlow

internal abstract class HomeViewModel : ViewModel() {

    abstract val recommendedSongsStateFlow: StateFlow<Result<List<SongItem>>>

    abstract val recentSongsStateFlow: StateFlow<Result<List<SongItem>>>

    abstract val ownSongsStateFlow: StateFlow<Result<List<SongItem>>>

    abstract fun fetchContent()

    abstract fun getCurrentTimeOfDay(): TimeOfDay

    abstract fun saveDefaultSong(context: Context)
}
