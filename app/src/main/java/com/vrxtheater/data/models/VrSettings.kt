package com.vrxtheater.data.models

/**
 * Represents all VR-related settings
 */
data class VrSettings(
    // Display settings
    val screenDistance: Float = 5.0f,
    val screenSize: Float = 16.0f,
    val screenCurvature: Float = 0.2f,
    val screenTilt: Float = 0.0f,
    val environmentBrightness: Float = 0.2f,
    
    // Lens calibration
    val ipd: Float = 63.0f, // Interpupillary distance in mm
    val lensOffsetX: Float = 0.0f,
    val lensOffsetY: Float = 0.0f,
    val barrelDistortion: Float = 0.5f,
    
    // Head tracking
    val trackingSmoothing: Float = 0.5f,
    val comfortMode: Boolean = false,
    
    // Audio
    val ambientSound: Boolean = true,
    val ambientVolume: Float = 0.3f,
    
    // Controller
    val controllerVibration: Boolean = true,
    
    // Performance
    val performanceMode: PerformanceMode = PerformanceMode.BALANCED,
    
    // Behavior
    val autoLaunchVr: Boolean = true
) {
    companion object {
        fun getDefault(): VrSettings = VrSettings()
    }
}

/**
 * Performance mode options
 */
enum class PerformanceMode {
    PERFORMANCE, // Prioritize framerate
    BALANCED,    // Balance between quality and performance
    QUALITY      // Prioritize visual quality
}