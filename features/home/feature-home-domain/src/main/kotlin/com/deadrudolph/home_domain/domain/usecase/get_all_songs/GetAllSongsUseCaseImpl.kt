package com.deadrudolph.home_domain.domain.usecase.get_all_songs

import com.deadrudolph.common_domain.model.SongItem
import com.deadrudolph.home_domain.domain.repository.HomeRepository
import com.deadrudolph.common_domain.model.Result
import javax.inject.Inject

class GetAllSongsUseCaseImpl @Inject constructor(
    private val homeRepository: HomeRepository
) : GetAllSongsUseCase {

    override suspend operator fun invoke(): Result<List<SongItem>> {
        return homeRepository.getAllSongs()
    }
}
