package com.deadrudolph.feature_builder.di.module

import com.deadrudolph.feature_builder.util.manager.SongBuilderTextFieldsManager
import dagger.Module
import dagger.Provides

@Module
internal class UtilsModule {

    @Provides
    fun provideSongBuilderTextFieldsManager(
    ): SongBuilderTextFieldsManager {
        return SongBuilderTextFieldsManager()
    }
}
