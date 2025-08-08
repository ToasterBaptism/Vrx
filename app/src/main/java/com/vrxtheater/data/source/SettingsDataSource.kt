package com.vrxtheater.data.source

import android.content.Context
import android.content.SharedPreferences
import com.vrxtheater.data.models.PerformanceMode
import com.vrxtheater.data.models.VrSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for accessing and storing VR settings
 */
@Singleton
class SettingsDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * Saves VR settings to shared preferences
     */
    fun saveSettings(settings: VrSettings) {
        sharedPreferences.edit().apply {
            // Display settings
            putFloat(KEY_SCREEN_DISTANCE, settings.screenDistance)
            putFloat(KEY_SCREEN_SIZE, settings.screenSize)
            putFloat(KEY_SCREEN_CURVATURE, settings.screenCurvature)
            putFloat(KEY_SCREEN_TILT, settings.screenTilt)
            putFloat(KEY_ENVIRONMENT_BRIGHTNESS, settings.environmentBrightness)
            
            // Lens calibration
            putFloat(KEY_IPD, settings.ipd)
            putFloat(KEY_LENS_OFFSET_X, settings.lensOffsetX)
            putFloat(KEY_LENS_OFFSET_Y, settings.lensOffsetY)
            putFloat(KEY_BARREL_DISTORTION, settings.barrelDistortion)
            
            // Head tracking
            putFloat(KEY_TRACKING_SMOOTHING, settings.trackingSmoothing)
            putBoolean(KEY_COMFORT_MODE, settings.comfortMode)
            
            // Audio
            putBoolean(KEY_AMBIENT_SOUND, settings.ambientSound)
            putFloat(KEY_AMBIENT_VOLUME, settings.ambientVolume)
            
            // Controller
            putBoolean(KEY_CONTROLLER_VIBRATION, settings.controllerVibration)
            
            // Performance
            putString(KEY_PERFORMANCE_MODE, settings.performanceMode.name)
            
            // Behavior
            putBoolean(KEY_AUTO_LAUNCH_VR, settings.autoLaunchVr)
            
            apply()
        }
    }
    
    /**
     * Loads VR settings from shared preferences
     */
    fun loadSettings(): VrSettings {
        return VrSettings(
            // Display settings
            screenDistance = sharedPreferences.getFloat(KEY_SCREEN_DISTANCE, 5.0f),
            screenSize = sharedPreferences.getFloat(KEY_SCREEN_SIZE, 16.0f),
            screenCurvature = sharedPreferences.getFloat(KEY_SCREEN_CURVATURE, 0.2f),
            screenTilt = sharedPreferences.getFloat(KEY_SCREEN_TILT, 0.0f),
            environmentBrightness = sharedPreferences.getFloat(KEY_ENVIRONMENT_BRIGHTNESS, 0.2f),
            
            // Lens calibration
            ipd = sharedPreferences.getFloat(KEY_IPD, 63.0f),
            lensOffsetX = sharedPreferences.getFloat(KEY_LENS_OFFSET_X, 0.0f),
            lensOffsetY = sharedPreferences.getFloat(KEY_LENS_OFFSET_Y, 0.0f),
            barrelDistortion = sharedPreferences.getFloat(KEY_BARREL_DISTORTION, 0.5f),
            
            // Head tracking
            trackingSmoothing = sharedPreferences.getFloat(KEY_TRACKING_SMOOTHING, 0.5f),
            comfortMode = sharedPreferences.getBoolean(KEY_COMFORT_MODE, false),
            
            // Audio
            ambientSound = sharedPreferences.getBoolean(KEY_AMBIENT_SOUND, true),
            ambientVolume = sharedPreferences.getFloat(KEY_AMBIENT_VOLUME, 0.3f),
            
            // Controller
            controllerVibration = sharedPreferences.getBoolean(KEY_CONTROLLER_VIBRATION, true),
            
            // Performance
            performanceMode = try {
                PerformanceMode.valueOf(
                    sharedPreferences.getString(KEY_PERFORMANCE_MODE, PerformanceMode.BALANCED.name)
                        ?: PerformanceMode.BALANCED.name
                )
            } catch (e: IllegalArgumentException) {
                PerformanceMode.BALANCED
            },
            
            // Behavior
            autoLaunchVr = sharedPreferences.getBoolean(KEY_AUTO_LAUNCH_VR, true)
        )
    }
    
    /**
     * Resets settings to default values
     */
    fun resetSettings() {
        saveSettings(VrSettings.getDefault())
    }
    
    companion object {
        private const val PREFERENCES_NAME = "vr_settings"
        
        // Display settings
        private const val KEY_SCREEN_DISTANCE = "screen_distance"
        private const val KEY_SCREEN_SIZE = "screen_size"
        private const val KEY_SCREEN_CURVATURE = "screen_curvature"
        private const val KEY_SCREEN_TILT = "screen_tilt"
        private const val KEY_ENVIRONMENT_BRIGHTNESS = "environment_brightness"
        
        // Lens calibration
        private const val KEY_IPD = "ipd"
        private const val KEY_LENS_OFFSET_X = "lens_offset_x"
        private const val KEY_LENS_OFFSET_Y = "lens_offset_y"
        private const val KEY_BARREL_DISTORTION = "barrel_distortion"
        
        // Head tracking
        private const val KEY_TRACKING_SMOOTHING = "tracking_smoothing"
        private const val KEY_COMFORT_MODE = "comfort_mode"
        
        // Audio
        private const val KEY_AMBIENT_SOUND = "ambient_sound"
        private const val KEY_AMBIENT_VOLUME = "ambient_volume"
        
        // Controller
        private const val KEY_CONTROLLER_VIBRATION = "controller_vibration"
        
        // Performance
        private const val KEY_PERFORMANCE_MODE = "performance_mode"
        
        // Behavior
        private const val KEY_AUTO_LAUNCH_VR = "auto_launch_vr"
    }
}