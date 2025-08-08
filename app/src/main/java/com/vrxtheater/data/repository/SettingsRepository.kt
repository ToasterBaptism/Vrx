package com.vrxtheater.data.repository

import com.vrxtheater.data.models.VrSettings
import com.vrxtheater.data.source.SettingsDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing and managing VR settings
 */
@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDataSource: SettingsDataSource
) {
    private val _settings = MutableStateFlow(settingsDataSource.loadSettings())
    val settings: Flow<VrSettings> = _settings.asStateFlow()
    
    /**
     * Updates the VR settings
     */
    fun updateSettings(settings: VrSettings) {
        settingsDataSource.saveSettings(settings)
        _settings.value = settings
    }
    
    /**
     * Updates a single setting
     */
    fun updateSetting(update: (VrSettings) -> VrSettings) {
        val updatedSettings = update(_settings.value)
        updateSettings(updatedSettings)
    }
    
    /**
     * Resets settings to default values
     */
    fun resetSettings() {
        settingsDataSource.resetSettings()
        _settings.value = settingsDataSource.loadSettings()
    }
}