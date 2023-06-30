package com.deadrudolph.feature_player.ui.screen.view

import android.view.MotionEvent
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.common_utils.extension.dpToPx
import com.deadrudolph.feature_player.ui.model.SongContentLayoutResult
import com.deadrudolph.uicomponents.R.drawable
import com.deadrudolph.uicomponents.compose.theme.CustomTheme
import com.deadrudolph.uicomponents.compose.view.ChordsBlock
import com.deadrudolph.uicomponents.compose.view.ChordsRow
import com.deadrudolph.uicomponents.compose.view.PlayerTextBlock
import com.deadrudolph.uicomponents.ui_model.SongState
import com.deadrudolph.uicomponents.utils.composition_locals.LocalContentSize
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
internal fun SongContent(
    songState: StateFlow<SongState>,
    playingState: StateFlow<Boolean>,
    playingTimeState: StateFlow<Int>,
    getScrollingStateValue: () -> Float,
    onStartPlayClicked: () -> Boolean,
    onStopPlayClicked: () -> Unit,
    onPausePlayClicked: () -> Unit,
    onChordClicked: (ChordType) -> Unit,
    onTimePickerClick: () -> Unit,
    onSongGloballyPositioned: (SongContentLayoutResult) -> Unit
) {
    val songComposeState = songState.collectAsState()

    val listState = rememberLazyListState()

    val coroutineScope = rememberCoroutineScope()

    val playingTimeStateValue = playingTimeState.collectAsState().value.also {
        val scrollingValue = getScrollingStateValue()
        if (scrollingValue != 0f) coroutineScope.launch {
            listState.animateScrollBy(scrollingValue)
        }
    }

    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val contentHeight = LocalContentSize.current.height
        val (song, controls) = createRefs()

        LazyColumn(
            modifier = Modifier
                .pointerInteropFilter { value ->
                    if (playingState.value && value.action == MotionEvent.ACTION_DOWN) {
                        onPausePlayClicked()
                    }
                    false
                }
                .constrainAs(song) {
                    bottom.linkTo(controls.top)
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    height = Dimension.fillToConstraints
                }
                .padding(top = 5.dp),
            horizontalAlignment = CenterHorizontally,
            contentPadding = PaddingValues(top = (contentHeight / 20).dp),
            state = listState
        ) {

            item {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(songComposeState.value.imagePath)
                        .build(),
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .size(120.dp, 120.dp)
                        .clip(RoundedCornerShape(15.dp)),
                    error = painterResource(id = drawable.img_song_default),
                    contentDescription = "Avatar",
                    contentScale = ContentScale.Fit,
                    placeholder = painterResource(id = drawable.img_song_default)
                )
            }

            item {
                Title(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(
                            bottom = 20.dp
                        ),
                    text = songComposeState.value.title
                )
            }

            item {
                Column(
                    modifier = Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .onGloballyPositioned { value ->
                            onSongGloballyPositioned(
                                SongContentLayoutResult(
                                    contentHeight = value.size.height,
                                    contentPositionY = value.positionInRoot().y.roundToInt(),
                                    screenHeight = contentHeight - DEFAULT_CONTROLS_HEIGHT
                                        .dpToPx
                                        .roundToInt()
                                )
                            )
                        }
                ) {
                    (songComposeState.value.textFields).forEach { textFieldState ->

                        ChordsBlock(
                            chordsBlock = textFieldState.chordBlock,
                            isEditable = false,
                            onChordClicked = { chordType, _ ->
                                onChordClicked(chordType)
                            }
                        )

                        ChordsRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(top = 5.dp, bottom = 1.dp),
                            chordsList = textFieldState.chordsList,
                            onChordClicked = { chord ->
                                onChordClicked(chord.chordType)
                            }
                        )

                        PlayerTextBlock(
                            modifier = Modifier
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .padding(
                                    all = 5.dp
                                ),
                            text = textFieldState.value.text
                        )

                    }
                }
            }
        }

        ControlsSection(
            modifier = Modifier
                .fillMaxWidth()
                .height(DEFAULT_CONTROLS_HEIGHT.dp)
                .background(color = CustomTheme.colors.dark_800)
                .padding(vertical = 7.dp)
                .constrainAs(controls) {
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            playingState = playingState,
            playingTimeStateValue = playingTimeStateValue,
            onStartPlayClicked = onStartPlayClicked,
            onStopPlayClicked = onStopPlayClicked,
            onPausePlayClicked = onPausePlayClicked,
            onTimePickerClick = onTimePickerClick
        )
    }
}

private const val DEFAULT_CONTROLS_HEIGHT = 90f
