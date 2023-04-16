package com.deadrudolph.home_domain.domain.usecase

import com.deadrudolph.home_domain.domain.model.songs_dashboard.SongItem
import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.puls.stateutil.Result
import javax.inject.Inject

class GetAllSongsUseCase @Inject constructor(
    private val homeRepository: HomeRepository
) {

    suspend operator fun invoke(): Result<List<SongItem>> {
        return homeRepository.getAllSongs()
    }
}