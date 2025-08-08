package com.vrxtheater.vr.util

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import org.rajawali3d.math.Quaternion
import org.rajawali3d.math.vector.Vector3
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Sensor fusion for head tracking using gyroscope, accelerometer, and magnetometer
 */
class SensorFusion(private val sensorManager: SensorManager) : SensorEventListener {
    
    // Sensors
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val gyroscope: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    
    // Sensor data
    private val accelerometerReading = FloatArray(3)
    private val gyroscopeReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    
    // Rotation matrices
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    
    // Quaternion for rotation
    private val rotation = Quaternion()
    private val gyroRotation = Quaternion()
    private val accelMagRotation = Quaternion()
    
    // Timing
    private var timestamp = 0L
    private var initState = true
    
    // Calibration
    private var calibrationQuaternion = Quaternion()
    
    // Complementary filter coefficient
    private var filterCoefficient = 0.98f
    
    /**
     * Starts sensor fusion
     */
    fun start() {
        // Register sensor listeners
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
        
        // Reset state
        initState = true
        timestamp = 0L
    }
    
    /**
     * Stops sensor fusion
     */
    fun stop() {
        sensorManager.unregisterListener(this)
    }
    
    /**
     * Resets the rotation to forward
     */
    fun resetRotation() {
        // Store current rotation as calibration
        calibrationQuaternion = rotation.inverse()
    }
    
    /**
     * Sets the filter coefficient for sensor fusion
     */
    fun setFilterCoefficient(coefficient: Float) {
        filterCoefficient = coefficient.coerceIn(0f, 1f)
    }
    
    /**
     * Returns the current rotation as a quaternion
     */
    fun getRotation(): Quaternion {
        // Apply calibration quaternion
        val calibratedRotation = Quaternion(rotation)
        calibratedRotation.multiply(calibrationQuaternion)
        return calibratedRotation
    }
    
    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
                updateOrientationAngles()
            }
            Sensor.TYPE_GYROSCOPE -> {
                handleGyroscopeEvent(event)
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
                updateOrientationAngles()
            }
        }
    }
    
    /**
     * Handles gyroscope sensor events
     */
    private fun handleGyroscopeEvent(event: SensorEvent) {
        // Copy gyroscope values
        System.arraycopy(event.values, 0, gyroscopeReading, 0, gyroscopeReading.size)
        
        // Initialize timestamp on first event
        if (timestamp != 0L) {
            // Calculate time difference in seconds
            val dT = (event.timestamp - timestamp) * NS2S
            
            // Calculate rotation rate
            val axisX = gyroscopeReading[0]
            val axisY = gyroscopeReading[1]
            val axisZ = gyroscopeReading[2]
            
            // Calculate rotation angle
            val omegaMagnitude = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)
            
            if (omegaMagnitude > EPSILON) {
                // Normalize rotation vector
                val rotationVectorX = axisX / omegaMagnitude
                val rotationVectorY = axisY / omegaMagnitude
                val rotationVectorZ = axisZ / omegaMagnitude
                
                // Calculate rotation angle
                val thetaOverTwo = omegaMagnitude * dT / 2.0f
                val sinThetaOverTwo = Math.sin(thetaOverTwo.toDouble()).toFloat()
                val cosThetaOverTwo = Math.cos(thetaOverTwo.toDouble()).toFloat()
                
                // Create rotation quaternion
                val deltaRotationW = cosThetaOverTwo
                val deltaRotationX = sinThetaOverTwo * rotationVectorX
                val deltaRotationY = sinThetaOverTwo * rotationVectorY
                val deltaRotationZ = sinThetaOverTwo * rotationVectorZ
                
                // Update gyro rotation
                val deltaRotation = Quaternion(deltaRotationW, deltaRotationX, deltaRotationY, deltaRotationZ)
                gyroRotation.multiply(deltaRotation)
                
                // Normalize quaternion
                gyroRotation.normalize()
            }
        }
        
        // Update timestamp
        timestamp = event.timestamp
        
        // Perform sensor fusion
        performSensorFusion()
    }
    
    /**
     * Updates orientation angles from accelerometer and magnetometer
     */
    private fun updateOrientationAngles() {
        // Check if we have valid readings
        if (accelerometerReading.any { abs(it) < EPSILON } || 
            magnetometerReading.any { abs(it) < EPSILON }) {
            return
        }
        
        // Calculate rotation matrix
        SensorManager.getRotationMatrix(
            rotationMatrix, null, accelerometerReading, magnetometerReading
        )
        
        // Get orientation angles
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        
        // Convert to quaternion
        val azimuth = orientationAngles[0]
        val pitch = orientationAngles[1]
        val roll = orientationAngles[2]
        
        // Create quaternion from Euler angles
        val qx = Math.sin(roll / 2.0).toFloat() * Math.cos(pitch / 2.0).toFloat() * Math.cos(azimuth / 2.0).toFloat() - 
                 Math.cos(roll / 2.0).toFloat() * Math.sin(pitch / 2.0).toFloat() * Math.sin(azimuth / 2.0).toFloat()
        
        val qy = Math.cos(roll / 2.0).toFloat() * Math.sin(pitch / 2.0).toFloat() * Math.cos(azimuth / 2.0).toFloat() + 
                 Math.sin(roll / 2.0).toFloat() * Math.cos(pitch / 2.0).toFloat() * Math.sin(azimuth / 2.0).toFloat()
        
        val qz = Math.cos(roll / 2.0).toFloat() * Math.cos(pitch / 2.0).toFloat() * Math.sin(azimuth / 2.0).toFloat() - 
                 Math.sin(roll / 2.0).toFloat() * Math.sin(pitch / 2.0).toFloat() * Math.cos(azimuth / 2.0).toFloat()
        
        val qw = Math.cos(roll / 2.0).toFloat() * Math.cos(pitch / 2.0).toFloat() * Math.cos(azimuth / 2.0).toFloat() + 
                 Math.sin(roll / 2.0).toFloat() * Math.sin(pitch / 2.0).toFloat() * Math.sin(azimuth / 2.0).toFloat()
        
        // Set accel/mag rotation
        accelMagRotation.setAll(qw, qx, qy, qz)
        accelMagRotation.normalize()
        
        // Initialize gyro rotation if needed
        if (initState) {
            gyroRotation.setAll(accelMagRotation)
            initState = false
        }
        
        // Perform sensor fusion
        performSensorFusion()
    }
    
    /**
     * Performs sensor fusion using complementary filter
     */
    private fun performSensorFusion() {
        // Apply complementary filter
        // Use gyro for short-term changes and accel/mag for long-term stability
        rotation.setAll(
            gyroRotation.w * filterCoefficient + accelMagRotation.w * (1 - filterCoefficient),
            gyroRotation.x * filterCoefficient + accelMagRotation.x * (1 - filterCoefficient),
            gyroRotation.y * filterCoefficient + accelMagRotation.y * (1 - filterCoefficient),
            gyroRotation.z * filterCoefficient + accelMagRotation.z * (1 - filterCoefficient)
        )
        
        // Normalize the result
        rotation.normalize()
    }
    
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Not used
    }
    
    companion object {
        private const val NS2S = 1.0f / 1000000000.0f // Nanoseconds to seconds
        private const val EPSILON = 0.000001f // Small value for comparisons
    }
}