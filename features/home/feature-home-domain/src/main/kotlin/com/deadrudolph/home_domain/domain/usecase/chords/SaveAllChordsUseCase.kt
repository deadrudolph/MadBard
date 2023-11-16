package com.deadrudolph.home_domain.domain.usecase.chords

import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.home_domain.domain.repository.HomeRepository

class SaveAllChordsUseCase(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(chords: List<ChordType>) {
        homeRepository.saveChords(*chords.toTypedArray())
    }
}
