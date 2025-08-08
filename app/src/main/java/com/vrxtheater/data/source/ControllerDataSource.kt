package com.vrxtheater.data.source

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.SharedPreferences
import android.hardware.input.InputManager
import android.hardware.usb.UsbDevice
import android.hardware.usb.UsbManager
import android.os.Build
import android.view.InputDevice
import com.vrxtheater.data.models.ConnectionType
import com.vrxtheater.data.models.ControllerButton
import com.vrxtheater.data.models.ControllerInfo
import com.vrxtheater.data.models.ControllerType
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Data source for accessing and managing game controllers
 */
@Singleton
class ControllerDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }
    
    private val _connectedControllers = MutableStateFlow<List<ControllerInfo>>(emptyList())
    val connectedControllers: Flow<List<ControllerInfo>> = _connectedControllers.asStateFlow()
    
    /**
     * Scans for connected controllers via USB and Bluetooth
     */
    fun scanForControllers() {
        val controllers = mutableListOf<ControllerInfo>()
        
        // Scan for USB controllers
        val usbManager = context.getSystemService(Context.USB_SERVICE) as UsbManager
        val usbDevices = usbManager.deviceList.values
        
        for (device in usbDevices) {
            if (isGameController(device)) {
                controllers.add(createControllerInfo(device))
            }
        }
        
        // Scan for Bluetooth controllers
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val bluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager
            val bluetoothAdapter = bluetoothManager?.adapter
            
            if (bluetoothAdapter != null && bluetoothAdapter.isEnabled) {
                val pairedDevices = bluetoothAdapter.bondedDevices
                
                for (device in pairedDevices) {
                    if (isGameController(device)) {
                        controllers.add(createControllerInfo(device))
                    }
                }
            }
        }
        
        // Scan for input devices
        val inputManager = context.getSystemService(Context.INPUT_SERVICE) as InputManager
        val inputDevices = InputDevice.getDeviceIds()
        
        for (deviceId in inputDevices) {
            val inputDevice = InputDevice.getDevice(deviceId)
            if (isGameController(inputDevice)) {
                // Check if we already added this controller
                val existingController = controllers.find { it.id == inputDevice.descriptor }
                if (existingController == null) {
                    controllers.add(createControllerInfo(inputDevice))
                }
            }
        }
        
        _connectedControllers.value = controllers
    }
    
    /**
     * Determines if a USB device is a game controller
     */
    private fun isGameController(device: UsbDevice): Boolean {
        // USB HID class code for game controllers is 0x03
        return device.deviceClass == 0x03 || 
               device.interfaceCount > 0 && device.getInterface(0).interfaceClass == 0x03
    }
    
    /**
     * Determines if a Bluetooth device is a game controller
     */
    private fun isGameController(device: BluetoothDevice): Boolean {
        // Check device class for game controllers
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                device.bluetoothClass.majorDeviceClass == BluetoothDevice.DEVICE_TYPE_CLASSIC &&
                device.name?.contains("controller", ignoreCase = true) == true
            } catch (e: SecurityException) {
                false
            }
        } else {
            @Suppress("DEPRECATION")
            device.bluetoothClass.majorDeviceClass == BluetoothDevice.DEVICE_TYPE_CLASSIC &&
            device.name?.contains("controller", ignoreCase = true) == true
        }
    }
    
    /**
     * Determines if an input device is a game controller
     */
    private fun isGameController(device: InputDevice): Boolean {
        // Check if the device has gamepad buttons and joysticks
        return (device.sources and InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD ||
               (device.sources and InputDevice.SOURCE_JOYSTICK) == InputDevice.SOURCE_JOYSTICK
    }
    
    /**
     * Creates a ControllerInfo object from a USB device
     */
    private fun createControllerInfo(device: UsbDevice): ControllerInfo {
        val id = device.deviceName
        val name = device.productName ?: "USB Controller"
        val type = determineControllerType(name)
        
        return ControllerInfo(
            id = id,
            name = name,
            type = type,
            connectionType = ConnectionType.USB,
            isConnected = true,
            buttonMapping = loadButtonMapping(id)
        )
    }
    
    /**
     * Creates a ControllerInfo object from a Bluetooth device
     */
    private fun createControllerInfo(device: BluetoothDevice): ControllerInfo {
        val id = device.address
        val name = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            try {
                device.name ?: "Bluetooth Controller"
            } catch (e: SecurityException) {
                "Bluetooth Controller"
            }
        } else {
            @Suppress("DEPRECATION")
            device.name ?: "Bluetooth Controller"
        }
        val type = determineControllerType(name)
        
        // Get battery level if available
        val batteryLevel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                device.batteryLevel
            } catch (e: SecurityException) {
                -1
            }
        } else {
            -1
        }
        
        return ControllerInfo(
            id = id,
            name = name,
            type = type,
            connectionType = ConnectionType.BLUETOOTH,
            batteryLevel = batteryLevel,
            isConnected = true,
            buttonMapping = loadButtonMapping(id)
        )
    }
    
    /**
     * Creates a ControllerInfo object from an input device
     */
    private fun createControllerInfo(device: InputDevice): ControllerInfo {
        val id = device.descriptor
        val name = device.name
        val type = determineControllerType(name)
        
        // Determine connection type
        val connectionType = when {
            name.contains("bluetooth", ignoreCase = true) -> ConnectionType.BLUETOOTH
            name.contains("usb", ignoreCase = true) -> ConnectionType.USB
            else -> ConnectionType.UNKNOWN
        }
        
        return ControllerInfo(
            id = id,
            name = name,
            type = type,
            connectionType = connectionType,
            isConnected = true,
            buttonMapping = loadButtonMapping(id)
        )
    }
    
    /**
     * Determines the controller type based on the name
     */
    private fun determineControllerType(name: String): ControllerType {
        return when {
            name.contains("xbox", ignoreCase = true) -> ControllerType.XBOX
            name.contains("playstation", ignoreCase = true) || 
            name.contains("dualshock", ignoreCase = true) ||
            name.contains("dualsense", ignoreCase = true) ||
            name.contains("ps", ignoreCase = true) -> ControllerType.PLAYSTATION
            name.contains("nintendo", ignoreCase = true) ||
            name.contains("switch", ignoreCase = true) -> ControllerType.NINTENDO
            else -> ControllerType.GENERIC
        }
    }
    
    /**
     * Saves button mappings for a controller
     */
    fun saveButtonMapping(controllerId: String, mapping: Map<Int, Int>) {
        val mappingString = mapping.entries.joinToString(",") { "${it.key}:${it.value}" }
        sharedPreferences.edit().putString("$KEY_BUTTON_MAPPING_PREFIX$controllerId", mappingString).apply()
    }
    
    /**
     * Loads button mappings for a controller
     */
    fun loadButtonMapping(controllerId: String): Map<Int, Int> {
        val mappingString = sharedPreferences.getString("$KEY_BUTTON_MAPPING_PREFIX$controllerId", "")
        
        return if (mappingString.isNullOrEmpty()) {
            getDefaultMapping()
        } else {
            mappingString.split(",").associate {
                val (key, value) = it.split(":")
                key.toInt() to value.toInt()
            }
        }
    }
    
    /**
     * Returns the default button mapping
     */
    private fun getDefaultMapping(): Map<Int, Int> {
        return mapOf(
            ControllerButton.A to ControllerButton.A,
            ControllerButton.B to ControllerButton.B,
            ControllerButton.X to ControllerButton.X,
            ControllerButton.Y to ControllerButton.Y,
            ControllerButton.L1 to ControllerButton.L1,
            ControllerButton.R1 to ControllerButton.R1,
            ControllerButton.L2 to ControllerButton.L2,
            ControllerButton.R2 to ControllerButton.R2,
            ControllerButton.L3 to ControllerButton.L3,
            ControllerButton.R3 to ControllerButton.R3,
            ControllerButton.DPAD_UP to ControllerButton.DPAD_UP,
            ControllerButton.DPAD_DOWN to ControllerButton.DPAD_DOWN,
            ControllerButton.DPAD_LEFT to ControllerButton.DPAD_LEFT,
            ControllerButton.DPAD_RIGHT to ControllerButton.DPAD_RIGHT,
            ControllerButton.START to ControllerButton.START,
            ControllerButton.SELECT to ControllerButton.SELECT,
            ControllerButton.HOME to ControllerButton.HOME
        )
    }
    
    /**
     * Resets button mappings to default for a controller
     */
    fun resetButtonMapping(controllerId: String) {
        sharedPreferences.edit().remove("$KEY_BUTTON_MAPPING_PREFIX$controllerId").apply()
    }
    
    companion object {
        private const val PREFERENCES_NAME = "controller_mappings"
        private const val KEY_BUTTON_MAPPING_PREFIX = "button_mapping_"
    }
}