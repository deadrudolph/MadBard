package com.deadrudolph.tuner.model.tuner

import com.deadrudolph.tuner.model.settings.AccidentalOption
import com.deadrudolph.tuner.model.settings.NotationOption
import com.deadrudolph.tuner.model.settings.Settings

data class Tuning(
    val note: ChromaticScale? = null,
    val frequency: Float = -1f,
    val deviation: TuningDeviationResult = TuningDeviationResult.NotDetected
) {

    val formattedFrequency by lazy { ChromaticScale.FREQUENCY_FORMAT.format(frequency) }

    fun getTone(settings: Settings): String {
        requireNotNull(note)

        return when {
            settings.accidental == AccidentalOption.FLAT &&
                settings.notation == NotationOption.DO_RE_MI &&
                note.semitone -> ChromaticScale.getSolfegeTone(ChromaticScale.getFlatTone(note.tone))

            settings.accidental == AccidentalOption.FLAT &&
                note.semitone -> ChromaticScale.getFlatTone(note.tone)

            settings.notation == NotationOption.DO_RE_MI -> ChromaticScale.getSolfegeTone(note.tone)

            else -> note.tone
        }
    }

    fun getSemitoneSymbolRes(settings: Settings): Int? {
        requireNotNull(note)

        return if (note.semitone) {
            settings.accidental.symbolRes
        } else {
            null
        }
    }
}
