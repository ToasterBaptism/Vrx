package com.vrxtheater.ui.calibration

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vrxtheater.R
import com.vrxtheater.data.models.VrSettings
import com.vrxtheater.ui.theme.VrxTheaterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalibrationScreen(
    viewModel: CalibrationViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.calibration_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.resetSettings() }) {
                        Icon(
                            imageVector = Icons.Filled.Refresh,
                            contentDescription = stringResource(R.string.reset)
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
            // Lens calibration section
            CalibrationSection(
                title = stringResource(R.string.calibration_lens_title),
                description = stringResource(R.string.calibration_lens_description)
            ) {
                // IPD slider
                SliderSetting(
                    title = stringResource(R.string.calibration_ipd),
                    value = settings.ipd,
                    valueRange = 50f..80f,
                    valueText = "${settings.ipd.toInt()} mm",
                    onValueChange = { viewModel.updateIpd(it) }
                )
                
                // Lens offset X slider
                SliderSetting(
                    title = stringResource(R.string.calibration_lens_offset_x),
                    value = settings.lensOffsetX,
                    valueRange = -20f..20f,
                    valueText = "${settings.lensOffsetX.toInt()} mm",
                    onValueChange = { viewModel.updateLensOffsetX(it) }
                )
                
                // Lens offset Y slider
                SliderSetting(
                    title = stringResource(R.string.calibration_lens_offset_y),
                    value = settings.lensOffsetY,
                    valueRange = -20f..20f,
                    valueText = "${settings.lensOffsetY.toInt()} mm",
                    onValueChange = { viewModel.updateLensOffsetY(it) }
                )
                
                // Barrel distortion slider
                SliderSetting(
                    title = stringResource(R.string.calibration_barrel_distortion),
                    value = settings.barrelDistortion,
                    valueRange = 0f..1f,
                    valueText = String.format("%.2f", settings.barrelDistortion),
                    onValueChange = { viewModel.updateBarrelDistortion(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Screen calibration section
            CalibrationSection(
                title = stringResource(R.string.calibration_screen_title),
                description = stringResource(R.string.calibration_screen_description)
            ) {
                // Screen distance slider
                SliderSetting(
                    title = stringResource(R.string.calibration_screen_distance),
                    value = settings.screenDistance,
                    valueRange = 1f..10f,
                    valueText = String.format("%.1f m", settings.screenDistance),
                    onValueChange = { viewModel.updateScreenDistance(it) }
                )
                
                // Screen size slider
                SliderSetting(
                    title = stringResource(R.string.calibration_screen_size),
                    value = settings.screenSize,
                    valueRange = 8f..24f,
                    valueText = String.format("%.1f m", settings.screenSize),
                    onValueChange = { viewModel.updateScreenSize(it) }
                )
                
                // Screen curvature slider
                SliderSetting(
                    title = stringResource(R.string.calibration_screen_curvature),
                    value = settings.screenCurvature,
                    valueRange = 0f..0.5f,
                    valueText = String.format("%.2f", settings.screenCurvature),
                    onValueChange = { viewModel.updateScreenCurvature(it) }
                )
                
                // Screen tilt slider
                SliderSetting(
                    title = stringResource(R.string.calibration_screen_tilt),
                    value = settings.screenTilt,
                    valueRange = -0.5f..0.5f,
                    valueText = String.format("%.2f", settings.screenTilt),
                    onValueChange = { viewModel.updateScreenTilt(it) }
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Test pattern
            CalibrationSection(
                title = stringResource(R.string.calibration_test_pattern_title),
                description = stringResource(R.string.calibration_test_pattern_description)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(
                        onClick = { viewModel.showTestPattern() }
                    ) {
                        Text(stringResource(R.string.calibration_show_test_pattern))
                    }
                    
                    Button(
                        onClick = { viewModel.testVrMode() }
                    ) {
                        Text(stringResource(R.string.calibration_test_vr_mode))
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Save button
            Button(
                onClick = { viewModel.saveSettings() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.calibration_save))
            }
        }
    }
}

@Composable
fun CalibrationSection(
    title: String,
    description: String,
    content: @Composable () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            content()
        }
    }
}

@Composable
fun SliderSetting(
    title: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueText: String,
    onValueChange: (Float) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            
            Text(
                text = valueText,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CalibrationScreenPreview() {
    VrxTheaterTheme {
        CalibrationScreen(
            onNavigateUp = {}
        )
    }
}