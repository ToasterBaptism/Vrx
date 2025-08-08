package com.vrxtheater.ui.settings

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.vrxtheater.data.models.PerformanceMode
import com.vrxtheater.data.models.VrSettings
import com.vrxtheater.data.repository.SettingsRepository
import com.vrxtheater.ui.settings.controller.ControllerMappingActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // Settings
    val settings: StateFlow<VrSettings> = settingsRepository.settings
    
    /**
     * Updates the performance mode setting
     */
    fun updatePerformanceMode(mode: PerformanceMode) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(performanceMode = mode)
        }
    }
    
    /**
     * Updates the comfort mode setting
     */
    fun updateComfortMode(enabled: Boolean) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(comfortMode = enabled)
        }
    }
    
    /**
     * Updates the tracking smoothing setting
     */
    fun updateTrackingSmoothing(smoothing: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(trackingSmoothing = smoothing)
        }
    }
    
    /**
     * Updates the environment brightness setting
     */
    fun updateEnvironmentBrightness(brightness: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(environmentBrightness = brightness)
        }
    }
    
    /**
     * Updates the ambient sound setting
     */
    fun updateAmbientSound(enabled: Boolean) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(ambientSound = enabled)
        }
    }
    
    /**
     * Updates the ambient volume setting
     */
    fun updateAmbientVolume(volume: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(ambientVolume = volume)
        }
    }
    
    /**
     * Updates the controller vibration setting
     */
    fun updateControllerVibration(enabled: Boolean) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(controllerVibration = enabled)
        }
    }
    
    /**
     * Updates the auto launch VR setting
     */
    fun updateAutoLaunchVr(enabled: Boolean) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(autoLaunchVr = enabled)
        }
    }
    
    /**
     * Navigates to the controller mapping screen
     */
    fun navigateToControllerMapping() {
        val intent = Intent(context, ControllerMappingActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}