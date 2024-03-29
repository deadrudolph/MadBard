package com.deadrudolph.feature_builder.di.component

import androidx.lifecycle.ViewModelProvider
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.commondi.module.CommonDiModule
import com.deadrudolph.feature_builder.di.dependencies.DependenciesImpl
import com.deadrudolph.feature_builder.di.module.MapperModule
import com.deadrudolph.feature_builder.di.module.UtilsModule
import com.deadrudolph.feature_builder.di.module.ViewModelModule
import com.deadrudolph.feature_builder_domain.domain.usecase.SaveSongUseCase
import com.deadrudolph.home_domain.domain.usecase.chords.GetAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import dagger.Component

interface SongBuilderComponent : DIComponent

@Component(
    modules = [
        CommonDiModule::class,
        ViewModelModule::class,
        MapperModule::class,
        UtilsModule::class
    ],
    dependencies = [
        SongBuilderComponentInternal.Dependencies::class,
    ]
)
internal interface SongBuilderComponentInternal :
    SongBuilderComponent,
    SongBuilderScreenComponent {

    fun getViewModelFactory(): ViewModelProvider.Factory

    interface Dependencies {
        val saveSongUseCase: SaveSongUseCase
        val getAllSongsUseCase: GetAllSongsUseCase
        val getAllChordsUseCase: GetAllChordsUseCase
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): SongBuilderComponentInternal
    }
}

object SongBuilderComponentHolder : FeatureComponentHolder<SongBuilderComponent>() {

    override fun build(): SongBuilderComponent {

        return DaggerSongBuilderComponentInternal.factory().create(
            DependenciesImpl(),
        )
    }

    internal fun getInternal(): SongBuilderComponentInternal = get() as SongBuilderComponentInternal
}
