package com.deadrudolph.home_domain.domain.usecase.chords

import com.deadrudolph.common_domain.model.ChordType
import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.deadrudolph.common_domain.model.Result

class GetAllChordsUseCase(
    private val homeRepo: HomeRepository
) {

    suspend operator fun invoke(): Result<List<ChordType>> {
        return homeRepo.getAllChords()
    }
}
