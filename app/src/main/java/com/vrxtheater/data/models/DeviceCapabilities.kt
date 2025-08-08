package com.vrxtheater.data.models

/**
 * Represents the VR capabilities of the device
 */
data class DeviceCapabilities(
    val hasGyroscope: Boolean = false,
    val hasAccelerometer: Boolean = false,
    val hasMagnetometer: Boolean = false,
    val screenWidth: Int = 0,
    val screenHeight: Int = 0,
    val refreshRate: Float = 60f,
    val deviceModel: String = "",
    val androidVersion: Int = 0,
    val isVrCapable: Boolean = false
) {
    /**
     * Returns whether the device meets the minimum requirements for VR
     */
    fun meetsMinimumRequirements(): Boolean {
        return hasGyroscope && hasAccelerometer && 
               screenWidth >= 1080 && screenHeight >= 1920
    }
    
    /**
     * Returns a list of missing requirements
     */
    fun getMissingRequirements(): List<String> {
        val missing = mutableListOf<String>()
        
        if (!hasGyroscope) missing.add("Gyroscope")
        if (!hasAccelerometer) missing.add("Accelerometer")
        if (screenWidth < 1080 || screenHeight < 1920) {
            missing.add("Screen resolution (minimum 1080x1920)")
        }
        
        return missing
    }
}