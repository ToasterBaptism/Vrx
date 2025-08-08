package com.vrxtheater.ui.calibration

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vrxtheater.data.models.VrSettings
import com.vrxtheater.data.repository.SettingsRepository
import com.vrxtheater.ui.vr.VrActivity
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CalibrationViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // Settings
    val settings: StateFlow<VrSettings> = settingsRepository.settings
    
    /**
     * Updates the IPD setting
     */
    fun updateIpd(ipd: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(ipd = ipd)
        }
    }
    
    /**
     * Updates the lens offset X setting
     */
    fun updateLensOffsetX(offsetX: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(lensOffsetX = offsetX)
        }
    }
    
    /**
     * Updates the lens offset Y setting
     */
    fun updateLensOffsetY(offsetY: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(lensOffsetY = offsetY)
        }
    }
    
    /**
     * Updates the barrel distortion setting
     */
    fun updateBarrelDistortion(distortion: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(barrelDistortion = distortion)
        }
    }
    
    /**
     * Updates the screen distance setting
     */
    fun updateScreenDistance(distance: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(screenDistance = distance)
        }
    }
    
    /**
     * Updates the screen size setting
     */
    fun updateScreenSize(size: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(screenSize = size)
        }
    }
    
    /**
     * Updates the screen curvature setting
     */
    fun updateScreenCurvature(curvature: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(screenCurvature = curvature)
        }
    }
    
    /**
     * Updates the screen tilt setting
     */
    fun updateScreenTilt(tilt: Float) {
        settingsRepository.updateSetting { currentSettings ->
            currentSettings.copy(screenTilt = tilt)
        }
    }
    
    /**
     * Resets all settings to default values
     */
    fun resetSettings() {
        settingsRepository.resetSettings()
    }
    
    /**
     * Saves the current settings
     */
    fun saveSettings() {
        viewModelScope.launch {
            // Settings are already saved in real-time, so this is just a placeholder
            // for any additional save operations if needed
        }
    }
    
    /**
     * Shows a test pattern in VR mode
     */
    fun showTestPattern() {
        val intent = Intent(context, VrActivity::class.java).apply {
            putExtra("test_pattern", true)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    /**
     * Tests VR mode with current settings
     */
    fun testVrMode() {
        val intent = Intent(context, VrActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
}