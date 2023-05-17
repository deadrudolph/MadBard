package com.deadrudolph.feature_builder_domain.domain.repository_abs

import com.deadrudolph.common_domain.model.SongItem

internal interface SongBuilderRepository {

    suspend fun saveSong(songItem: SongItem)
}