package com.deadrudolph.feature_builder.di.component

import androidx.lifecycle.ViewModelProvider
import com.deadrudolph.commondi.component.base.DIComponent
import com.deadrudolph.commondi.holder.single.FeatureComponentHolder
import com.deadrudolph.commondi.module.CommonDiModule
import com.deadrudolph.feature_builder.di.dependencies.DependenciesImpl
import com.deadrudolph.feature_builder.di.module.ViewModelModule
import dagger.Component

@Component
interface SongBuilderComponent: DIComponent {
}
@Component(
    modules = [
        CommonDiModule::class,
        ViewModelModule::class
    ],
    dependencies = [
        SongBuilderComponentInternal.Dependencies::class,
    ]
)
internal interface SongBuilderComponentInternal :
    SongBuilderComponent,
    SongBuilderScreenComponent {

    fun getViewModelFactory(): ViewModelProvider.Factory

    interface Dependencies

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