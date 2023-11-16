package com.deadrudolph.home.presentation.ui.screen.home.main

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordGroup
import com.deadrudolph.common_domain.model.ChordGroup.E
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.common_domain.model.getDefaultChordsList
import com.deadrudolph.common_utils.file_utils.FileManager
import com.deadrudolph.home_domain.domain.model.time_of_day.TimeOfDay
import com.deadrudolph.home_domain.domain.usecase.chords.GetAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.chords.SaveAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.save_songs.SaveSongsUseCase
import com.deadrudolph.uicomponents.R.drawable
import com.puls.stateutil.Result
import com.puls.stateutil.Result.Loading
import com.puls.stateutil.Result.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

internal class HomeViewModelImpl @Inject constructor(
    private val saveSongsUseCase: SaveSongsUseCase,
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val saveAllChordsUseCase: SaveAllChordsUseCase,
    private val getAllChordsUseCase: GetAllChordsUseCase
) : HomeViewModel() {

    override val recommendedSongsStateFlow = MutableStateFlow<Result<List<SongItem>>>(
        Loading(false)
    )

    override val recentSongsStateFlow = MutableStateFlow<Result<List<SongItem>>>(
        Loading(false)
    )

    override val ownSongsStateFlow = MutableStateFlow<Result<List<SongItem>>>(
        Loading(false)
    )

    override fun saveDefaultSong(context: Context) {
        viewModelScope.launch {
            val bitmap = BitmapFactory.decodeResource(
                context.resources,
                drawable.img_song_default
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
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/" +
                        "app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText",
                    createTimeMillis = 0L
                )
            )
        }
    }

    override fun fetchContent() {
        viewModelScope.launch {
            saveSongsUseCase(
                SongItem(
                    id = "id1",
                    createTimeMillis = 0L,
                    title = "songWithBlocks",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = listOf(
                        Chord(
                            chordType = ChordType("Em", listOf(0, 0, 0, 2, 2, 0), E),
                            position = 65,
                            positionOverlapCharCount = 0
                        ),
                        Chord(
                            chordType = ChordType("A", listOf(0, 2, 2, 2, 0, 0), ChordGroup.A),
                            position = 153
                        )
                    ),
                    text = "someText (id1) someText (id1) someText (id1) someText (id1) someText " +
                        "(id1) someText (id1) someText (id1) someText (id1) someText (id1) " +
                        "someText (id1) someText (id1) someText (id1) someText (id1) " +
                        "someText (id1) someText (id1) someText (id1) someText (id1)",
                    chordBlocks = listOf()
                ),
                SongItem(
                    id = "id2",
                    createTimeMillis = 0L,
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/" +
                        "app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id3",
                    createTimeMillis = 0L,
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/" +
                        "app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id4",
                    createTimeMillis = 0L,
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/" +
                        "app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id5",
                    createTimeMillis = 0L,
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/" +
                        "app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText"
                ),
                SongItem(
                    id = "id6",
                    createTimeMillis = 0L,
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/" +
                        "app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText(id6)"
                ),
                SongItem(
                    id = "id6",
                    createTimeMillis = 0L,
                    title = "someTitle",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/" +
                        "app_imageDir/DefaultImage",
                    chords = emptyList(),
                    text = "someText(id6)"
                )
            )
            saveDefaultChordsIfNeeded()
            setLoadingIfNeeded()
            val allSongs = getAllSongsUseCase()
            recommendedSongsStateFlow.value = allSongs
            recentSongsStateFlow.value = allSongs
            ownSongsStateFlow.value = allSongs
        }
    }

    private suspend fun saveDefaultChordsIfNeeded() {
        if ((getAllChordsUseCase() as? Success)?.data.isNullOrEmpty()) {
            saveAllChordsUseCase(getDefaultChordsList())
        }
    }

    private fun setLoadingIfNeeded() {
        recommendedSongsStateFlow.setLoadingIfNoData()
        recentSongsStateFlow.setLoadingIfNoData()
        ownSongsStateFlow.setLoadingIfNoData()
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

    private fun MutableStateFlow<Result<List<SongItem>>>.setLoadingIfNoData() {
        if (value is Success) return
        value = Loading(true)
    }
}
