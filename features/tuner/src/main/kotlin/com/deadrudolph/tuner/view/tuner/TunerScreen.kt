package com.deadrudolph.tuner.view.tuner

import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BackdropScaffold
import androidx.compose.material.BackdropValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberBackdropScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import cafe.adriel.voyager.androidx.AndroidScreen
import cafe.adriel.voyager.androidx.AndroidScreenLifecycleOwner
import com.deadrudolph.commondi.util.getDaggerViewModel
import com.deadrudolph.tuner.R
import com.deadrudolph.tuner.di.component.TunerComponentHolder
import com.deadrudolph.tuner.ktx.keepScreenOn
import com.deadrudolph.tuner.ktx.openExternalAppSettings
import com.deadrudolph.tuner.ktx.toggle
import com.deadrudolph.tuner.model.settings.AccidentalOption
import com.deadrudolph.tuner.model.settings.DeviationPrecisionOption
import com.deadrudolph.tuner.model.settings.NotationOption
import com.deadrudolph.tuner.model.settings.PitchDetectionAlgorithmOption
import com.deadrudolph.tuner.model.settings.Settings
import com.deadrudolph.tuner.model.tuner.Tuning
import com.deadrudolph.tuner.model.tuner.TuningDeviationResult
import com.deadrudolph.tuner.view.components.MessageSnackbar
import com.deadrudolph.tuner.view.components.RequestPermissionSnackbar
import com.deadrudolph.tuner.view.components.SelectPreference
import com.deadrudolph.tuner.view.components.SwitchPreference
import com.deadrudolph.tuner.view.components.TuningDeviationBars
import com.deadrudolph.tuner.view.components.TuningInfo
import com.deadrudolph.tuner.view.components.TuningNote
import com.deadrudolph.uicomponents.R.drawable
import com.deadrudolph.uicomponents.compose.theme.DefaultTheme

class TunerScreen : AndroidScreen() {

    @OptIn(ExperimentalMaterialApi::class)
    @Composable
    override fun Content() {
        val viewModel =
            getDaggerViewModel<TunerViewModel>(
                viewModelProviderFactory = TunerComponentHolder.getInternal()
                    .getViewModelFactory(),
                isSharedViewModel = true
            )
        val screenState by viewModel.state.collectAsState()
        val scaffoldState = rememberBackdropScaffoldState(BackdropValue.Revealed)
        val scope = rememberCoroutineScope()
        val context = LocalContext.current

        (context as? ComponentActivity)?.keepScreenOn()

        SideEffect {
            viewModel.updatePermissionState()
        }

        DefaultTheme {
            BackdropScaffold(
                appBar = {
                    TunerTopBar(onSettingsClicked = { scaffoldState.toggle(scope) })
                },
                backLayerContent = {
                    TunerContent(screenState.tuning, screenState.settings)

                    if (screenState.hasRequiredPermissions.not()) {
                        RequestPermissionSnackbar(
                            scaffoldState.snackbarHostState,
                            context::openExternalAppSettings
                        )
                    }

                    screenState.message?.let { message ->
                        MessageSnackbar(
                            message,
                            scaffoldState.snackbarHostState,
                            viewModel::consumeMessage
                        )
                    }
                },
                frontLayerContent = {
                    TunerSettings(screenState.settings, viewModel)
                },
                headerHeight = Dp.Hairline,
                scaffoldState = scaffoldState
            )
        }
    }

    @Composable
    private fun TunerTopBar(onSettingsClicked: () -> Unit) =
        TopAppBar(
            title = {},
            actions = {
                IconButton(
                    onClick = onSettingsClicked,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = drawable.ic_settings),
                        contentDescription = "Icon Settings"
                    )
                }
            },
            backgroundColor = Color.Transparent,
            elevation = Dp.Hairline
        )

    @Composable
    private fun TunerContent(tuning: Tuning, settings: Settings) =
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (noteRef, deviationBarsRef, infoRef) = createRefs()
            val verticalGuideline = createGuidelineFromTop(.55f)

            if (tuning.deviation is TuningDeviationResult.Detected && tuning.note != null) {
                TuningNote(
                    note = tuning.note,
                    tone = tuning.getTone(settings),
                    accidental = tuning.getSemitoneSymbolRes(settings)?.let { painterResource(it) },
                    advancedMode = settings.advancedMode,
                    modifier = Modifier.constrainAs(noteRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(parent.top)
                        bottom.linkTo(deviationBarsRef.top)
                    }
                )
            }

            TuningDeviationBars(
                deviationResult = tuning.deviation,
                modifier = Modifier
                    .padding(top = 32.dp, bottom = 24.dp)
                    .constrainAs(deviationBarsRef) {
                        centerAround(verticalGuideline)
                    }
            )

            if (tuning.deviation is TuningDeviationResult.Detected && tuning.note != null && settings.advancedMode) {
                TuningInfo(
                    deviation = tuning.deviation.value,
                    frequency = tuning.formattedFrequency,
                    color = tuning.deviation.precision.color,
                    modifier = Modifier.constrainAs(infoRef) {
                        centerHorizontallyTo(parent)
                        top.linkTo(deviationBarsRef.bottom)
                    }
                )
            }
        }

    @Composable
    private fun TunerSettings(
        settings: Settings,
        viewModel: TunerViewModel
    ) {
        val activity = LocalContext.current as? ComponentActivity
        LaunchedEffect(Unit) {
            (getLifecycleOwner() as? AndroidScreenLifecycleOwner)?.let {
                activity?.let { viewModel.requestPermissions(activity) }
                viewModel.startTuner(it)
            }
        }

        LazyColumn {
            item {
                SwitchPreference(
                    title = stringResource(R.string.advanced_mode),
                    subtitle = stringResource(R.string.show_secondary_data),
                    checked = settings.advancedMode,
                    onChanged = {
                        viewModel.updateSettings(settings.copy(advancedMode = settings.advancedMode.not()))
                    }
                )
                SwitchPreference(
                    title = stringResource(R.string.noise_suppressor),
                    subtitle = stringResource(R.string.removes_background_noise),
                    checked = settings.noiseSuppressor,
                    onChanged = {
                        viewModel.updateSettings(settings.copy(noiseSuppressor = settings.noiseSuppressor.not()))
                    }
                )
                SelectPreference(
                    title = stringResource(R.string.notation),
                    selected = settings.notation,
                    options = NotationOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(notation = it))
                    }
                )
                SelectPreference(
                    title = stringResource(R.string.accidental),
                    selected = settings.accidental,
                    options = AccidentalOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(accidental = it))
                    }
                )
                SelectPreference(
                    title = stringResource(R.string.precision),
                    selected = settings.deviationPrecision,
                    options = DeviationPrecisionOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(deviationPrecision = it))
                    }
                )
                SelectPreference(
                    title = stringResource(R.string.pitch_detection_algorithm),
                    selected = settings.pitchDetectionAlgorithm,
                    options = PitchDetectionAlgorithmOption.values(),
                    onSelected = {
                        viewModel.updateSettings(settings.copy(pitchDetectionAlgorithm = it))
                    }
                )
            }
        }
    }
}
