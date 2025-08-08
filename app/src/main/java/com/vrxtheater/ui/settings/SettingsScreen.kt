package com.vrxtheater.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vrxtheater.R
import com.vrxtheater.data.models.PerformanceMode
import com.vrxtheater.ui.theme.VrxTheaterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit
) {
    val settings by viewModel.settings.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
        ) {
            // Performance settings
            SettingsCategory(title = stringResource(R.string.settings_performance))
            
            SettingsRadioOption(
                title = stringResource(R.string.settings_performance_quality),
                subtitle = stringResource(R.string.settings_performance_quality_description),
                selected = settings.performanceMode == PerformanceMode.QUALITY,
                onClick = { viewModel.updatePerformanceMode(PerformanceMode.QUALITY) }
            )
            
            SettingsRadioOption(
                title = stringResource(R.string.settings_performance_balanced),
                subtitle = stringResource(R.string.settings_performance_balanced_description),
                selected = settings.performanceMode == PerformanceMode.BALANCED,
                onClick = { viewModel.updatePerformanceMode(PerformanceMode.BALANCED) }
            )
            
            SettingsRadioOption(
                title = stringResource(R.string.settings_performance_battery),
                subtitle = stringResource(R.string.settings_performance_battery_description),
                selected = settings.performanceMode == PerformanceMode.BATTERY,
                onClick = { viewModel.updatePerformanceMode(PerformanceMode.BATTERY) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Comfort settings
            SettingsCategory(title = stringResource(R.string.settings_comfort))
            
            SettingsSwitchOption(
                title = stringResource(R.string.settings_comfort_mode),
                subtitle = stringResource(R.string.settings_comfort_mode_description),
                checked = settings.comfortMode,
                onCheckedChange = { viewModel.updateComfortMode(it) }
            )
            
            SettingsSliderOption(
                title = stringResource(R.string.settings_tracking_smoothing),
                value = settings.trackingSmoothing,
                valueRange = 0f..1f,
                valueText = String.format("%.2f", settings.trackingSmoothing),
                onValueChange = { viewModel.updateTrackingSmoothing(it) }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Environment settings
            SettingsCategory(title = stringResource(R.string.settings_environment))
            
            SettingsSliderOption(
                title = stringResource(R.string.settings_environment_brightness),
                value = settings.environmentBrightness,
                valueRange = 0f..1f,
                valueText = String.format("%.2f", settings.environmentBrightness),
                onValueChange = { viewModel.updateEnvironmentBrightness(it) }
            )
            
            SettingsSwitchOption(
                title = stringResource(R.string.settings_ambient_sound),
                subtitle = stringResource(R.string.settings_ambient_sound_description),
                checked = settings.ambientSound,
                onCheckedChange = { viewModel.updateAmbientSound(it) }
            )
            
            if (settings.ambientSound) {
                SettingsSliderOption(
                    title = stringResource(R.string.settings_ambient_volume),
                    value = settings.ambientVolume,
                    valueRange = 0f..1f,
                    valueText = String.format("%.2f", settings.ambientVolume),
                    onValueChange = { viewModel.updateAmbientVolume(it) }
                )
            }
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Controller settings
            SettingsCategory(title = stringResource(R.string.settings_controller))
            
            SettingsSwitchOption(
                title = stringResource(R.string.settings_controller_vibration),
                subtitle = stringResource(R.string.settings_controller_vibration_description),
                checked = settings.controllerVibration,
                onCheckedChange = { viewModel.updateControllerVibration(it) }
            )
            
            SettingsActionOption(
                title = stringResource(R.string.settings_controller_mapping),
                subtitle = stringResource(R.string.settings_controller_mapping_description),
                onClick = { viewModel.navigateToControllerMapping() }
            )
            
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            
            // Behavior settings
            SettingsCategory(title = stringResource(R.string.settings_behavior))
            
            SettingsSwitchOption(
                title = stringResource(R.string.settings_auto_launch_vr),
                subtitle = stringResource(R.string.settings_auto_launch_vr_description),
                checked = settings.autoLaunchVr,
                onCheckedChange = { viewModel.updateAutoLaunchVr(it) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}