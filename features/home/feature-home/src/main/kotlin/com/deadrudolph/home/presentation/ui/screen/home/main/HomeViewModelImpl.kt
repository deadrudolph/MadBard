package com.deadrudolph.home.presentation.ui.screen.home.main

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_utils.file_utils.FileManager
import com.deadrudolph.feature_home.R
import com.deadrudolph.home_domain.domain.model.songs_dashboard.SongItem
import com.deadrudolph.home_domain.domain.model.time_of_day.TimeOfDay
import com.deadrudolph.home_domain.domain.usecase.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase
import com.puls.stateutil.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

internal class HomeViewModelImpl @Inject constructor(
    private val saveSongsUseCase: SaveSongsUseCase,
    private val getAllSongsUseCase: GetAllSongsUseCase
) : HomeViewModel() {

    override val recommendedSongsStateFlow = MutableStateFlow<Result<List<SongItem>>>(
        Result.Loading(false)
    )

    override val recentSongsStateFlow = MutableStateFlow<Result<List<SongItem>>>(
        Result.Loading(false)
    )

    override val ownSongsStateFlow = MutableStateFlow<Result<List<SongItem>>>(
        Result.Loading(false)
    )

    override fun saveDefaultSong(context: Context) {
        viewModelScope.launch {
            val bitmap = BitmapFactory.decodeResource(
                context.resources,
                R.drawable.img_song_default
            )
            val path = FileManager.saveBitmap(
                contextWrapper = ContextWrapper(context),
                bitmap = bitmap,
                fileName = DEFAULT_IMAGE_NAME
            )
            saveSongsUseCase(
                SongItem(
                    id = "id",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                )
            )
        }
    }

    override fun fetchContent() {
        viewModelScope.launch {
            saveSongsUseCase(
                SongItem(
                    id = "id1",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText (id1)"
                ),
                SongItem(
                    id = "id2",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id3",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id4",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id5",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id6",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText(id6)"
                ),
                SongItem(
                    id = "id6",
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText(id6)"
                )
            )
            recommendedSongsStateFlow.value = Result.Loading(true)
            recentSongsStateFlow.value = Result.Loading(true)
            ownSongsStateFlow.value = Result.Loading(true)
            delay(2000)
            val allSongs = getAllSongsUseCase()
            recommendedSongsStateFlow.value = allSongs
            recentSongsStateFlow.value = allSongs
            ownSongsStateFlow.value = allSongs
        }
    }

    override fun getCurrentTimeOfDay(): TimeOfDay {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.HOUR_OF_DAY)) {
            in 0..3 -> TimeOfDay.EVENING
            in 4..11 -> TimeOfDay.MORNING
            in 12..15 -> TimeOfDay.DAY
            in 16..24 -> TimeOfDay.EVENING
            else -> TimeOfDay.DAY
        }
    }

    private companion object {
        const val DEFAULT_IMAGE_NAME = "DefaultImage"
    }
}