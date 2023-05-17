package com.deadrudolph.home_domain.domain.usecase

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.home_domain.domain.repository.HomeRepository
import javax.inject.Inject

class SaveSongsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(vararg songs: SongItem) {
        homeRepository.saveSongs(*songs)
    }
}