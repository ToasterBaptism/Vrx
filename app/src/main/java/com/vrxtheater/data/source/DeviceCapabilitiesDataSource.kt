package com.vrxtheater.data.source

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.vrxtheater.data.models.DeviceCapabilities
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for accessing device capabilities
 */
@Singleton
class DeviceCapabilitiesDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    /**
     * Returns the device capabilities
     */
    fun getDeviceCapabilities(): DeviceCapabilities {
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        
        // Check for required sensors
        val hasGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null
        val hasAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null
        val hasMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        
        // Get screen metrics
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val display = context.display
            display?.getRealMetrics(displayMetrics)
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        }
        
        val screenWidth = displayMetrics.widthPixels
        val screenHeight = displayMetrics.heightPixels
        
        // Get refresh rate
        val refreshRate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.display?.refreshRate ?: 60f
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.refreshRate
        }
        
        // Get device model and Android version
        val deviceModel = Build.MODEL
        val androidVersion = Build.VERSION.SDK_INT
        
        // Determine if the device is VR capable
        val isVrCapable = hasGyroscope && hasAccelerometer && 
                          screenWidth >= 1080 && screenHeight >= 1920
        
        return DeviceCapabilities(
            hasGyroscope = hasGyroscope,
            hasAccelerometer = hasAccelerometer,
            hasMagnetometer = hasMagnetometer,
            screenWidth = screenWidth,
            screenHeight = screenHeight,
            refreshRate = refreshRate,
            deviceModel = deviceModel,
            androidVersion = androidVersion,
            isVrCapable = isVrCapable
        )
    }
}