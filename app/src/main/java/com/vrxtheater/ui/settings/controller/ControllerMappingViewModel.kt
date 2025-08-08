package com.vrxtheater.ui.settings.controller

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.InputDevice
import android.view.KeyEvent
import androidx.core.content.getSystemService
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vrxtheater.data.models.ControllerButtonMapping
import com.vrxtheater.data.models.ControllerInfo
import com.vrxtheater.data.repository.ControllerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ControllerMappingViewModel @Inject constructor(
    private val controllerRepository: ControllerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // Available controllers
    val availableControllers: List<ControllerInfo>
        get() = controllerRepository.connectedControllers.value
    
    // Selected controller
    private val _selectedController = MutableStateFlow<ControllerInfo?>(null)
    val selectedController: StateFlow<ControllerInfo?> = _selectedController.asStateFlow()
    
    // Controller mappings
    private val _controllerMappings = MutableStateFlow<Map<ControllerButtonMapping, String>>(emptyMap())
    val controllerMappings: StateFlow<Map<ControllerButtonMapping, String>> = _controllerMappings.asStateFlow()
    
    // Listening for input
    private val _isListeningForInput = MutableStateFlow(false)
    val isListeningForInput: StateFlow<Boolean> = _isListeningForInput.asStateFlow()
    
    // Current button being mapped
    var currentMappingButton: ControllerButtonMapping? = null
        private set
    
    // Vibrator service for haptic feedback
    private val vibrator = context.getSystemService<Vibrator>()
    
    init {
        // Scan for controllers
        scanForControllers()
        
        // Load default mappings
        loadDefaultMappings()
    }
    
    /**
     * Scans for connected controllers
     */
    fun scanForControllers() {
        controllerRepository.scanForControllers()
        
        // Select the first controller if available and none is selected
        viewModelScope.launch {
            if (_selectedController.value == null && availableControllers.isNotEmpty()) {
                _selectedController.value = availableControllers.first()
                loadMappingsForController(_selectedController.value!!)
            }
        }
    }
    
    /**
     * Selects a controller
     */
    fun selectController(controller: ControllerInfo) {
        _selectedController.value = controller
        loadMappingsForController(controller)
    }
    
    /**
     * Loads mappings for the selected controller
     */
    private fun loadMappingsForController(controller: ControllerInfo) {
        viewModelScope.launch {
            // In a real implementation, this would load mappings from a database or preferences
            // For now, we'll use default mappings
            loadDefaultMappings()
        }
    }
    
    /**
     * Loads default mappings
     */
    private fun loadDefaultMappings() {
        val defaultMappings = mapOf(
            ControllerButtonMapping.BUTTON_A to "Button A",
            ControllerButtonMapping.BUTTON_B to "Button B",
            ControllerButtonMapping.BUTTON_X to "Button X",
            ControllerButtonMapping.BUTTON_Y to "Button Y",
            ControllerButtonMapping.DPAD_UP to "D-Pad Up",
            ControllerButtonMapping.DPAD_DOWN to "D-Pad Down",
            ControllerButtonMapping.DPAD_LEFT to "D-Pad Left",
            ControllerButtonMapping.DPAD_RIGHT to "D-Pad Right",
            ControllerButtonMapping.BUTTON_L1 to "L1",
            ControllerButtonMapping.BUTTON_R1 to "R1",
            ControllerButtonMapping.BUTTON_L2 to "L2",
            ControllerButtonMapping.BUTTON_R2 to "R2",
            ControllerButtonMapping.BUTTON_L3 to "L3",
            ControllerButtonMapping.BUTTON_R3 to "R3",
            ControllerButtonMapping.BUTTON_START to "Start",
            ControllerButtonMapping.BUTTON_SELECT to "Select"
        )
        
        _controllerMappings.value = defaultMappings
    }
    
    /**
     * Starts listening for button input
     */
    fun startListeningForButton(button: ControllerButtonMapping) {
        currentMappingButton = button
        _isListeningForInput.value = true
        
        // Provide haptic feedback
        vibrator?.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
    }
    
    /**
     * Cancels listening for button input
     */
    fun cancelListening() {
        _isListeningForInput.value = false
        currentMappingButton = null
    }
    
    /**
     * Handles key event from controller
     */
    fun handleKeyEvent(event: KeyEvent): Boolean {
        if (!_isListeningForInput.value || currentMappingButton == null) {
            return false
        }
        
        // Only process key down events
        if (event.action != KeyEvent.ACTION_DOWN) {
            return false
        }
        
        // Check if the event is from a game controller
        val device = InputDevice.getDevice(event.deviceId)
        if (device == null || !device.isVirtual && device.sources and InputDevice.SOURCE_GAMEPAD != InputDevice.SOURCE_GAMEPAD) {
            return false
        }
        
        // Get the key code name
        val keyName = KeyEvent.keyCodeToString(event.keyCode)
        
        // Update the mapping
        val updatedMappings = _controllerMappings.value.toMutableMap()
        updatedMappings[currentMappingButton!!] = keyName
        _controllerMappings.value = updatedMappings
        
        // Provide haptic feedback
        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        
        // Stop listening
        _isListeningForInput.value = false
        currentMappingButton = null
        
        return true
    }
    
    /**
     * Resets controller mappings to default
     */
    fun resetControllerMappings() {
        loadDefaultMappings()
        
        // Provide haptic feedback
        vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
    }
    
    /**
     * Saves controller mappings
     */
    fun saveControllerMappings() {
        viewModelScope.launch {
            // In a real implementation, this would save mappings to a database or preferences
            // For now, we'll just provide haptic feedback
            vibrator?.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}