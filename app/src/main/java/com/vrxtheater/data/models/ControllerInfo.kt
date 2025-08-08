package com.vrxtheater.data.models

/**
 * Represents information about a connected controller
 */
data class ControllerInfo(
    val id: String,
    val name: String,
    val type: ControllerType,
    val connectionType: ConnectionType,
    val batteryLevel: Int = -1, // -1 means unknown
    val isConnected: Boolean = false,
    val buttonMapping: Map<Int, Int> = emptyMap()
) {
    /**
     * Returns whether the controller has battery information
     */
    fun hasBatteryInfo(): Boolean = batteryLevel >= 0
    
    /**
     * Returns the battery status category
     */
    fun getBatteryStatus(): BatteryStatus {
        return when {
            batteryLevel < 0 -> BatteryStatus.UNKNOWN
            batteryLevel < 20 -> BatteryStatus.LOW
            batteryLevel < 50 -> BatteryStatus.MEDIUM
            else -> BatteryStatus.HIGH
        }
    }
}

/**
 * Controller connection types
 */
enum class ConnectionType {
    BLUETOOTH,
    USB,
    UNKNOWN
}

/**
 * Controller types for predefined mappings
 */
enum class ControllerType {
    XBOX,
    PLAYSTATION,
    NINTENDO,
    GENERIC,
    UNKNOWN
}

/**
 * Battery status categories
 */
enum class BatteryStatus {
    LOW,
    MEDIUM,
    HIGH,
    UNKNOWN
}

/**
 * Standard controller button mappings
 */
object ControllerButton {
    const val A = 0
    const val B = 1
    const val X = 2
    const val Y = 3
    const val L1 = 4
    const val R1 = 5
    const val L2 = 6
    const val R2 = 7
    const val L3 = 8
    const val R3 = 9
    const val DPAD_UP = 10
    const val DPAD_DOWN = 11
    const val DPAD_LEFT = 12
    const val DPAD_RIGHT = 13
    const val START = 14
    const val SELECT = 15
    const val HOME = 16
}