package com.deadrudolph.tuner.manager

import cafe.adriel.satchel.SatchelStorage
import cafe.adriel.satchel.ktx.value
import com.deadrudolph.tuner.model.settings.AccidentalOption
import com.deadrudolph.tuner.model.settings.DeviationPrecisionOption
import com.deadrudolph.tuner.model.settings.NotationOption
import com.deadrudolph.tuner.model.settings.PitchDetectionAlgorithmOption
import com.deadrudolph.tuner.model.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class SettingsManager @Inject constructor(
    storage: SatchelStorage
) {

    private val _state by lazy { MutableStateFlow(settings) }
    val state by lazy { _state.asStateFlow() }
    private val scope = CoroutineScope(Dispatchers.IO)

    var tunerAdvancedMode by storage.value(
        key = "tuner_advanced_mode",
        defaultValue = true
    )
    var tunerNoiseSuppressor by storage.value(
        key = "tuner_noise_suppressor",
        defaultValue = false
    )
    var tunerNotation by storage.value(
        key = "tuner_notation",
        defaultValue = NotationOption.A_B_C
    )
    var tunerAccidental by storage.value(
        key = "tuner_accidental",
        defaultValue = AccidentalOption.SHARP
    )
    var tunerPitchDetectionAlgorithm by storage.value(
        key = "tuner_pitch_detection_algorithm",
        defaultValue = PitchDetectionAlgorithmOption.FFT_YIN
    )
    var tunerDeviationPrecision by storage.value(
        key = "tuner_deviation_precision",
        defaultValue = DeviationPrecisionOption.Two
    )

    var settings: Settings
        get() = Settings(
            advancedMode = tunerAdvancedMode,
            noiseSuppressor = tunerNoiseSuppressor,
            notation = tunerNotation,
            accidental = tunerAccidental,
            deviationPrecision = tunerDeviationPrecision,
            pitchDetectionAlgorithm = tunerPitchDetectionAlgorithm
        )
        set(value) {
            tunerAdvancedMode = value.advancedMode
            tunerNoiseSuppressor = value.noiseSuppressor
            tunerNotation = value.notation
            tunerAccidental = value.accidental
            tunerDeviationPrecision = value.deviationPrecision
            tunerPitchDetectionAlgorithm = value.pitchDetectionAlgorithm
        }

    init {
        storage.addListener(scope) {
            _state.value = settings
        }
    }
}
