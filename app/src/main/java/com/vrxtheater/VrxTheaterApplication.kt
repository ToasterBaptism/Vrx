package com.vrxtheater

import android.app.Application
import android.content.Context
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class VrxTheaterApplication : Application() {

    companion object {
        private var instance: VrxTheaterApplication? = null

        fun getInstance(): VrxTheaterApplication {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    /**
     * Provides access to the sensor manager
     */
    fun getSensorManager(): SensorManager {
        return getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    /**
     * Provides access to the vibrator service with compatibility for different Android versions
     */
    fun getVibrator(): Vibrator {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    /**
     * Performs a short vibration for haptic feedback
     */
    fun vibrateForFeedback() {
        val vibrator = getVibrator()
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }
}