package com.deadrudolph.home_domain.di.component

import com.deadrudolph.commondatabase.dao.ChordsDao
import com.deadrudolph.commondatabase.dao.SongsDao
import com.deadrudolph.commondatabase.mapper.ChordEntityToChordMapper
import com.deadrudolph.commondatabase.mapper.ChordTypeToChordEntityMapper
import com.deadrudolph.commondatabase.mapper.SongEntityToSongItemMapper
import com.deadrudolph.commondatabase.mapper.SongItemToSongEntityMapper
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.home_domain.di.dependencies.DependenciesImpl
import com.deadrudolph.home_domain.di.module.NetworkModule
import com.deadrudolph.home_domain.di.module.RepositoryModule
import com.deadrudolph.home_domain.di.module.UseCaseModule
import com.deadrudolph.home_domain.domain.usecase.chords.GetAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.chords.SaveAllChordsUseCase
import com.deadrudolph.home_domain.domain.usecase.get_all_songs.GetAllSongsUseCase
import com.deadrudolph.home_domain.domain.usecase.save_songs.SaveSongsUseCase
import dagger.Component
import retrofit2.Retrofit

interface HomeDomainComponent : DIComponent {

    fun saveSongsUseCase(): SaveSongsUseCase

    fun getAllSongsUseCase(): GetAllSongsUseCase

    fun getAllChordsUseCase(): GetAllChordsUseCase

    fun saveAllChordsUseCase(): SaveAllChordsUseCase
}

@Component(
    modules = [
        NetworkModule::class,
        RepositoryModule::class,
        UseCaseModule::class
    ],

    dependencies = [
        HomeDomainComponentInternal.Dependencies::class,
    ]
)
internal interface HomeDomainComponentInternal : HomeDomainComponent {

    interface Dependencies {
        val retrofit: Retrofit
        val songsDao: SongsDao
        val chordsDao: ChordsDao
        val songEntityToSongItemMapper: SongEntityToSongItemMapper
        val songItemToSongEntityMapper: SongItemToSongEntityMapper
        val chordTypeToChordEntityMapper: ChordTypeToChordEntityMapper
        val chordEntityToChordMapper: ChordEntityToChordMapper
    }

    @Component.Factory
    interface Factory {
        fun create(
            dependencies: Dependencies
        ): HomeDomainComponentInternal
    }
}

object HomeDomainComponentHolder : FeatureComponentHolder<HomeDomainComponent>() {

    override fun build(): HomeDomainComponent {

        return DaggerHomeDomainComponentInternal.factory().create(
            DependenciesImpl()
        )
    }

    internal fun getInternal(): HomeDomainComponentInternal = get() as HomeDomainComponentInternal
}
