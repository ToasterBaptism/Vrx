package com.vrxtheater.ui.diagnostics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vrxtheater.R
import com.vrxtheater.ui.theme.VrxTheaterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosticsScreen(
    viewModel: DiagnosticsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val deviceCapabilities by viewModel.deviceCapabilities.collectAsState()
    val controllers by viewModel.controllers.collectAsState()
    val isRunningTests by viewModel.isRunningTests.collectAsState()
    val testResults by viewModel.testResults.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.diagnostics_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.refresh)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Device capabilities section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.diagnostics_device_capabilities),
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Device model
                    DiagnosticsRow(
                        label = stringResource(R.string.diagnostics_device_model),
                        value = deviceCapabilities.deviceModel
                    )
                    
                    // Android version
                    DiagnosticsRow(
                        label = stringResource(R.string.diagnostics_android_version),
                        value = "API ${deviceCapabilities.androidVersion}"
                    )
                    
                    // Screen resolution
                    DiagnosticsRow(
                        label = stringResource(R.string.diagnostics_screen_resolution),
                        value = "${deviceCapabilities.screenWidth} x ${deviceCapabilities.screenHeight}"
                    )
                    
                    // Refresh rate
                    DiagnosticsRow(
                        label = stringResource(R.string.diagnostics_refresh_rate),
                        value = "${deviceCapabilities.refreshRate} Hz"
                    )
                    
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    // Sensors
                    Text(
                        text = stringResource(R.string.diagnostics_sensors),
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // Gyroscope
                    DiagnosticsCheckRow(
                        label = stringResource(R.string.diagnostics_gyroscope),
                        isAvailable = deviceCapabilities.hasGyroscope
                    )
                    
                    // Accelerometer
                    DiagnosticsCheckRow(
                        label = stringResource(R.string.diagnostics_accelerometer),
                        isAvailable = deviceCapabilities.hasAccelerometer
                    )
                    
                    // Magnetometer
                    DiagnosticsCheckRow(
                        label = stringResource(R.string.diagnostics_magnetometer),
                        isAvailable = deviceCapabilities.hasMagnetometer
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    // VR capable
                    DiagnosticsCheckRow(
                        label = stringResource(R.string.diagnostics_vr_capable),
                        isAvailable = deviceCapabilities.isVrCapable,
                        description = if (!deviceCapabilities.isVrCapable) {
                            deviceCapabilities.getMissingRequirements().joinToString(", ")
                        } else null
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Controllers section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.diagnostics_controllers),
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (controllers.isEmpty()) {
                        Text(
                            text = stringResource(R.string.diagnostics_no_controllers),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        controllers.forEach { controller ->
                            DiagnosticsRow(
                                label = controller.name,
                                value = "${controller.type} (${controller.connectionType})"
                            )
                            
                            if (controller.batteryLevel >= 0) {
                                DiagnosticsRow(
                                    label = stringResource(R.string.diagnostics_battery),
                                    value = "${controller.batteryLevel}%"
                                )
                            }
                            
                            Divider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { viewModel.scanForControllers() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.diagnostics_scan_controllers))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Diagnostics tests section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = stringResource(R.string.diagnostics_tests),
                        style = MaterialTheme.typography.titleLarge
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    if (isRunningTests) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(end = 16.dp)
                            )
                            Text(
                                text = stringResource(R.string.diagnostics_running_tests),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    } else if (testResults.isEmpty()) {
                        Text(
                            text = stringResource(R.string.diagnostics_no_tests),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        testResults.forEach { (test, result) ->
                            DiagnosticsTestRow(
                                test = test,
                                passed = result
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { viewModel.runDiagnosticTests() },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isRunningTests
                    ) {
                        Text(stringResource(R.string.diagnostics_run_tests))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Export diagnostics button
            Button(
                onClick = { viewModel.exportDiagnostics() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.diagnostics_export))
            }
        }
    }
}

@Composable
fun DiagnosticsRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun DiagnosticsCheckRow(
    label: String,
    isAvailable: Boolean,
    description: String? = null
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            
            if (isAvailable) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.Green
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.Close,
                    contentDescription = null,
                    tint = Color.Red
                )
            }
        }
        
        if (description != null) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun DiagnosticsTestRow(
    test: String,
    passed: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = test,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        
        if (passed) {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = Color.Green
            )
        } else {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DiagnosticsScreenPreview() {
    VrxTheaterTheme {
        DiagnosticsScreen(
            onNavigateUp = {}
        )
    }
}