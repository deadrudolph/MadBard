package com.deadrudolph.feature_builder.di.module

import com.deadrudolph.feature_builder.util.mapper.TextFieldStateListToTextAndChordsMapper
import dagger.Module
import dagger.Provides

@Module
internal class MapperModule {

    @Provides
    fun provideTextFieldStateListToTextAndChordsMapper(
    ): TextFieldStateListToTextAndChordsMapper {
        return TextFieldStateListToTextAndChordsMapper()
    }
}