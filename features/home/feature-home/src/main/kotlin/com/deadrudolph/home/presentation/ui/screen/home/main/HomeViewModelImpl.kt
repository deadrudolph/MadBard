package com.deadrudolph.home.presentation.ui.screen.home.main

import android.content.Context
import android.content.ContextWrapper
import android.graphics.BitmapFactory
import androidx.lifecycle.viewModelScope
import com.deadrudolph.common_domain.model.Chord
import com.deadrudolph.common_domain.model.ChordBlock
import com.deadrudolph.common_domain.model.ChordType.A6
import com.deadrudolph.common_domain.model.ChordType.A7
import com.deadrudolph.common_domain.model.ChordType.A76
import com.deadrudolph.common_domain.model.ChordType.Am
import com.deadrudolph.common_domain.model.ChordType.Dm
import com.deadrudolph.common_domain.model.ChordType.E
import com.deadrudolph.common_domain.model.ChordType.Em
import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.common_utils.file_utils.FileManager
import com.deadrudolph.home_domain.domain.model.time_of_day.TimeOfDay
import com.deadrudolph.home_domain.domain.usecase.SaveSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.uicomponents.R.drawable
import com.puls.stateutil.Result
import com.puls.stateutil.Result.Loading
import com.puls.stateutil.Result.Success
import java.util.Calendar
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

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
                    title = "songWithBlocks",
                    imagePath = "/data/user/0/com.deadrudolph.composemultitemplate.dev/app_imageDir/DefaultImage",
                    chords = listOf(
                        Chord(
                            chordType = Em,
                            position = 65
                        ),
                        Chord(
                            chordType = Am,
                            position = 153
                        )
                    ),
                    text = "someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1) someText (id1)",
                    chordBlocks = listOf(
                        ChordBlock(
                            index = 1,
                            title = "ChordsList:",
                            chordsList = listOf(Am, Dm, Em, E, Am, A6, Em, Am, A6, A7, A76, Am, Em),
                            position = 0
                        ),
                        ChordBlock(
                            index = 2,
                            title = "ChordsList:",
                            chordsList = listOf(Am, Dm, Em, E, Am, A6, Em, Am, A6, A7, A76, Am, Em),
                            position = 0
                        )
                    )
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
            setLoadingIfNeeded()
            val allSongs = getAllSongsUseCase()
            recommendedSongsStateFlow.value = allSongs
            recentSongsStateFlow.value = allSongs
            ownSongsStateFlow.value = allSongs
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