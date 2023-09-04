package com.deadrudolph.tuner.model.settings

import androidx.annotation.StringRes
import be.tarsos.dsp.pitch.PitchProcessor.PitchEstimationAlgorithm
import com.deadrudolph.tuner.R
import com.deadrudolph.tuner.view.components.SelectOption

enum class PitchDetectionAlgorithmOption(
    @StringRes override val labelRes: Int,
    val algorithm: PitchEstimationAlgorithm
) : SelectOption<PitchDetectionAlgorithmOption> {
    YIN(R.string.pitch_detection_algorithm_yin, PitchEstimationAlgorithm.YIN),
    FFT_YIN(R.string.pitch_detection_algorithm_fft_yin, PitchEstimationAlgorithm.FFT_YIN),
    MPM(R.string.pitch_detection_algorithm_mpm, PitchEstimationAlgorithm.MPM),
    AMDF(R.string.pitch_detection_algorithm_amdf, PitchEstimationAlgorithm.AMDF),
    DYWA(R.string.pitch_detection_algorithm_dywa, PitchEstimationAlgorithm.DYNAMIC_WAVELET);
}
