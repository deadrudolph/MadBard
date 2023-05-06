package com.deadrudolph.commondatabase.di.module

import android.content.Context
import androidx.room.Room
import com.deadrudolph.commondatabase.constants.DBConstants
import com.deadrudolph.commondatabase.database.MadBardDatabase
import com.deadrudolph.commondatabase.type_converter.GeneralTypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideGeneralTypeConverter(): GeneralTypeConverter {
        return GeneralTypeConverter(
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()
        )
    }

    @Singleton
    @Provides
    fun provideDatabase(
        androidContext: Context,
        converter: GeneralTypeConverter
    ): MadBardDatabase {
        return Room.databaseBuilder(
            context = androidContext,
            klass = MadBardDatabase::class.java,
            name = DBConstants.DATABASE_NAME
        ).addTypeConverter(converter).build()
    }
}