package com.vrxtheater.data.repository

import com.vrxtheater.data.models.DeviceCapabilities
import com.vrxtheater.data.source.DeviceCapabilitiesDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing device capabilities
 */
@Singleton
class DeviceCapabilitiesRepository @Inject constructor(
    private val deviceCapabilitiesDataSource: DeviceCapabilitiesDataSource
) {
    private val _deviceCapabilities = MutableStateFlow(deviceCapabilitiesDataSource.getDeviceCapabilities())
    val deviceCapabilities: Flow<DeviceCapabilities> = _deviceCapabilities.asStateFlow()
    
    /**
     * Refreshes the device capabilities
     */
    fun refreshDeviceCapabilities() {
        _deviceCapabilities.value = deviceCapabilitiesDataSource.getDeviceCapabilities()
    }
    
    /**
     * Returns whether the device meets the minimum requirements for VR
     */
    fun meetsMinimumRequirements(): Boolean {
        return _deviceCapabilities.value.meetsMinimumRequirements()
    }
    
    /**
     * Returns a list of missing requirements
     */
    fun getMissingRequirements(): List<String> {
        return _deviceCapabilities.value.getMissingRequirements()
    }
}