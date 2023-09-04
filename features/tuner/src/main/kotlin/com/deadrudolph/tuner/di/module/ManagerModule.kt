package com.deadrudolph.tuner.di.module

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import cafe.adriel.satchel.Satchel
import cafe.adriel.satchel.SatchelStorage
import cafe.adriel.satchel.storer.file.FileSatchelStorer
import com.deadrudolph.tuner.manager.MessagingManager
import com.deadrudolph.tuner.manager.PermissionManager
import com.deadrudolph.tuner.manager.SettingsManager
import com.deadrudolph.tuner.manager.TunerManager
import dagger.Module
import dagger.Provides
import java.io.File
import javax.inject.Singleton

@Module
class ManagerModule {

    @Provides
    fun getTunerManager(
        settingsManager: SettingsManager,
        permissionManager: PermissionManager
    ): TunerManager = TunerManager(settingsManager, permissionManager)

    @Provides
    fun getPermissionManager(context: Context) = PermissionManager(context)

    @Provides
    fun provideMessagingManager(context: Context) = MessagingManager(context)

    @Provides
    @Singleton
    fun provideSettingsManager(
        storage: SatchelStorage
    ): SettingsManager = SettingsManager(storage)

    @Provides
    @Singleton
    fun provideSatchelStorage(context: Context): SatchelStorage = Satchel.with(
        storer = FileSatchelStorer(
            file = File(context.filesDir, "settings.storage")
        )
    )
}
