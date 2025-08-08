package com.vrxtheater.di

import android.content.Context
import com.vrxtheater.data.repository.ControllerRepository
import com.vrxtheater.data.repository.DeviceCapabilitiesRepository
import com.vrxtheater.data.repository.GameRepository
import com.vrxtheater.data.repository.SettingsRepository
import com.vrxtheater.data.source.ControllerDataSource
import com.vrxtheater.data.source.DeviceCapabilitiesDataSource
import com.vrxtheater.data.source.GameDataSource
import com.vrxtheater.data.source.SettingsDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Dagger Hilt module for providing application-level dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    // Data Sources
    
    @Provides
    @Singleton
    fun provideGameDataSource(@ApplicationContext context: Context): GameDataSource {
        return GameDataSource(context)
    }
    
    @Provides
    @Singleton
    fun provideSettingsDataSource(@ApplicationContext context: Context): SettingsDataSource {
        return SettingsDataSource(context)
    }
    
    @Provides
    @Singleton
    fun provideControllerDataSource(@ApplicationContext context: Context): ControllerDataSource {
        return ControllerDataSource(context)
    }
    
    @Provides
    @Singleton
    fun provideDeviceCapabilitiesDataSource(@ApplicationContext context: Context): DeviceCapabilitiesDataSource {
        return DeviceCapabilitiesDataSource(context)
    }
    
    // Repositories
    
    @Provides
    @Singleton
    fun provideGameRepository(gameDataSource: GameDataSource): GameRepository {
        return GameRepository(gameDataSource)
    }
    
    @Provides
    @Singleton
    fun provideSettingsRepository(settingsDataSource: SettingsDataSource): SettingsRepository {
        return SettingsRepository(settingsDataSource)
    }
    
    @Provides
    @Singleton
    fun provideControllerRepository(controllerDataSource: ControllerDataSource): ControllerRepository {
        return ControllerRepository(controllerDataSource)
    }
    
    @Provides
    @Singleton
    fun provideDeviceCapabilitiesRepository(
        deviceCapabilitiesDataSource: DeviceCapabilitiesDataSource
    ): DeviceCapabilitiesRepository {
        return DeviceCapabilitiesRepository(deviceCapabilitiesDataSource)
    }
}