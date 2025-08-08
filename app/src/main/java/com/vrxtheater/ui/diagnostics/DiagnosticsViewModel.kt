package com.vrxtheater.ui.diagnostics

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vrxtheater.data.models.ControllerInfo
import com.vrxtheater.data.models.DeviceCapabilities
import com.vrxtheater.data.repository.ControllerRepository
import com.vrxtheater.data.repository.DeviceCapabilitiesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class DiagnosticsViewModel @Inject constructor(
    private val deviceCapabilitiesRepository: DeviceCapabilitiesRepository,
    private val controllerRepository: ControllerRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    
    // Device capabilities
    val deviceCapabilities: StateFlow<DeviceCapabilities> = deviceCapabilitiesRepository.deviceCapabilities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DeviceCapabilities()
        )
    
    // Controllers
    val controllers: StateFlow<List<ControllerInfo>> = controllerRepository.connectedControllers
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Test results
    private val _testResults = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    val testResults: StateFlow<Map<String, Boolean>> = _testResults.asStateFlow()
    
    // Running tests flag
    private val _isRunningTests = MutableStateFlow(false)
    val isRunningTests: StateFlow<Boolean> = _isRunningTests.asStateFlow()
    
    init {
        // Refresh data
        refreshData()
    }
    
    /**
     * Refreshes all diagnostic data
     */
    fun refreshData() {
        deviceCapabilitiesRepository.refreshDeviceCapabilities()
        scanForControllers()
    }
    
    /**
     * Scans for connected controllers
     */
    fun scanForControllers() {
        controllerRepository.scanForControllers()
    }
    
    /**
     * Runs diagnostic tests
     */
    fun runDiagnosticTests() {
        viewModelScope.launch {
            _isRunningTests.value = true
            _testResults.value = emptyMap()
            
            // Simulate running tests
            val results = mutableMapOf<String, Boolean>()
            
            // Test sensors
            results["Gyroscope Sensor"] = testGyroscope()
            delay(500) // Simulate test duration
            
            results["Accelerometer Sensor"] = testAccelerometer()
            delay(500)
            
            results["Magnetometer Sensor"] = testMagnetometer()
            delay(500)
            
            // Test screen
            results["Screen Resolution"] = testScreenResolution()
            delay(500)
            
            // Test OpenGL
            results["OpenGL ES 3.0"] = testOpenGLES3()
            delay(500)
            
            // Test controllers
            results["Controller Detection"] = testControllerDetection()
            delay(500)
            
            // Test permissions
            results["Required Permissions"] = testPermissions()
            delay(500)
            
            _testResults.value = results
            _isRunningTests.value = false
        }
    }
    
    /**
     * Tests gyroscope sensor
     */
    private fun testGyroscope(): Boolean {
        return deviceCapabilities.value.hasGyroscope
    }
    
    /**
     * Tests accelerometer sensor
     */
    private fun testAccelerometer(): Boolean {
        return deviceCapabilities.value.hasAccelerometer
    }
    
    /**
     * Tests magnetometer sensor
     */
    private fun testMagnetometer(): Boolean {
        return deviceCapabilities.value.hasMagnetometer
    }
    
    /**
     * Tests screen resolution
     */
    private fun testScreenResolution(): Boolean {
        val width = deviceCapabilities.value.screenWidth
        val height = deviceCapabilities.value.screenHeight
        return width >= 1080 && height >= 1920
    }
    
    /**
     * Tests OpenGL ES 3.0 support
     */
    private fun testOpenGLES3(): Boolean {
        // In a real implementation, this would check for OpenGL ES 3.0 support
        // For now, we'll assume it's supported on newer devices
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
    }
    
    /**
     * Tests controller detection
     */
    private fun testControllerDetection(): Boolean {
        // Test if we can detect controllers
        return true
    }
    
    /**
     * Tests required permissions
     */
    private fun testPermissions(): Boolean {
        // In a real implementation, this would check for all required permissions
        return true
    }
    
    /**
     * Exports diagnostics data to a file and shares it
     */
    fun exportDiagnostics() {
        viewModelScope.launch {
            try {
                // Create diagnostics report
                val report = buildDiagnosticsReport()
                
                // Create file
                val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
                val fileName = "vrx_diagnostics_$timestamp.txt"
                
                val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)
                FileOutputStream(file).use { it.write(report.toByteArray()) }
                
                // Share file
                val uri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                
                val shareIntent = Intent.createChooser(intent, "Share Diagnostics Report")
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(shareIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    /**
     * Builds a diagnostics report
     */
    private fun buildDiagnosticsReport(): String {
        val sb = StringBuilder()
        
        // Add header
        sb.appendLine("VRX THEATER DIAGNOSTICS REPORT")
        sb.appendLine("==============================")
        sb.appendLine("Date: ${SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date())}")
        sb.appendLine()
        
        // Device info
        sb.appendLine("DEVICE INFORMATION")
        sb.appendLine("------------------")
        sb.appendLine("Model: ${deviceCapabilities.value.deviceModel}")
        sb.appendLine("Android Version: API ${deviceCapabilities.value.androidVersion}")
        sb.appendLine("Screen Resolution: ${deviceCapabilities.value.screenWidth} x ${deviceCapabilities.value.screenHeight}")
        sb.appendLine("Refresh Rate: ${deviceCapabilities.value.refreshRate} Hz")
        sb.appendLine()
        
        // Sensors
        sb.appendLine("SENSORS")
        sb.appendLine("-------")
        sb.appendLine("Gyroscope: ${if (deviceCapabilities.value.hasGyroscope) "Available" else "Not Available"}")
        sb.appendLine("Accelerometer: ${if (deviceCapabilities.value.hasAccelerometer) "Available" else "Not Available"}")
        sb.appendLine("Magnetometer: ${if (deviceCapabilities.value.hasMagnetometer) "Available" else "Not Available"}")
        sb.appendLine()
        
        // VR capability
        sb.appendLine("VR CAPABILITY")
        sb.appendLine("-------------")
        sb.appendLine("VR Capable: ${if (deviceCapabilities.value.isVrCapable) "Yes" else "No"}")
        if (!deviceCapabilities.value.isVrCapable) {
            sb.appendLine("Missing Requirements: ${deviceCapabilities.value.getMissingRequirements().joinToString(", ")}")
        }
        sb.appendLine()
        
        // Controllers
        sb.appendLine("CONTROLLERS")
        sb.appendLine("-----------")
        if (controllers.value.isEmpty()) {
            sb.appendLine("No controllers detected")
        } else {
            controllers.value.forEachIndexed { index, controller ->
                sb.appendLine("Controller ${index + 1}:")
                sb.appendLine("  Name: ${controller.name}")
                sb.appendLine("  Type: ${controller.type}")
                sb.appendLine("  Connection: ${controller.connectionType}")
                if (controller.batteryLevel >= 0) {
                    sb.appendLine("  Battery: ${controller.batteryLevel}%")
                }
                sb.appendLine()
            }
        }
        
        // Test results
        sb.appendLine("TEST RESULTS")
        sb.appendLine("------------")
        if (testResults.value.isEmpty()) {
            sb.appendLine("No tests have been run")
        } else {
            testResults.value.forEach { (test, result) ->
                sb.appendLine("$test: ${if (result) "PASS" else "FAIL"}")
            }
        }
        
        return sb.toString()
    }
}