package com.deadrudolph.home_domain.domain.usecase.get_all_songs

import com.deadrudolph.common_domain.model.SongItem
import com.puls.stateutil.Result

interface GetAllSongsUseCase {

    suspend operator fun invoke(): Result<List<SongItem>>
}