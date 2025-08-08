package com.vrxtheater.data.repository

import com.vrxtheater.data.models.ControllerInfo
import com.vrxtheater.data.source.ControllerDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing and managing game controllers
 */
@Singleton
class ControllerRepository @Inject constructor(
    private val controllerDataSource: ControllerDataSource
) {
    val connectedControllers: Flow<List<ControllerInfo>> = controllerDataSource.connectedControllers
    
    /**
     * Scans for connected controllers
     */
    fun scanForControllers() {
        controllerDataSource.scanForControllers()
    }
    
    /**
     * Saves button mappings for a controller
     */
    fun saveButtonMapping(controllerId: String, mapping: Map<Int, Int>) {
        controllerDataSource.saveButtonMapping(controllerId, mapping)
    }
    
    /**
     * Loads button mappings for a controller
     */
    fun loadButtonMapping(controllerId: String): Map<Int, Int> {
        return controllerDataSource.loadButtonMapping(controllerId)
    }
    
    /**
     * Resets button mappings to default for a controller
     */
    fun resetButtonMapping(controllerId: String) {
        controllerDataSource.resetButtonMapping(controllerId)
    }
}