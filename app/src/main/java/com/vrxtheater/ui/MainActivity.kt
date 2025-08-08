package com.vrxtheater.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.vrxtheater.data.repository.DeviceCapabilitiesRepository
import com.vrxtheater.data.repository.GameRepository
import com.vrxtheater.ui.main.MainScreen
import com.vrxtheater.ui.theme.VrxTheaterTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    
    @Inject
    lateinit var deviceCapabilitiesRepository: DeviceCapabilitiesRepository
    
    @Inject
    lateinit var gameRepository: GameRepository
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check device capabilities
        deviceCapabilitiesRepository.refreshDeviceCapabilities()
        
        // Load games
        lifecycleScope.launch {
            gameRepository.refreshGames()
        }
        
        setContent {
            VrxTheaterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen(
                        onNavigateToVr = { packageName ->
                            navigateToVr(packageName)
                        },
                        onNavigateToSettings = {
                            navigateToSettings()
                        },
                        onNavigateToCalibration = {
                            navigateToCalibration()
                        },
                        onNavigateToDiagnostics = {
                            navigateToDiagnostics()
                        }
                    )
                }
            }
        }
    }
    
    private fun navigateToVr(packageName: String?) {
        val intent = Intent(this, com.vrxtheater.ui.vr.VrActivity::class.java)
        packageName?.let {
            intent.putExtra("package_name", it)
        }
        startActivity(intent)
    }
    
    private fun navigateToSettings() {
        val intent = Intent(this, com.vrxtheater.ui.settings.SettingsActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToCalibration() {
        val intent = Intent(this, com.vrxtheater.ui.calibration.CalibrationActivity::class.java)
        startActivity(intent)
    }
    
    private fun navigateToDiagnostics() {
        val intent = Intent(this, com.vrxtheater.ui.diagnostics.DiagnosticsActivity::class.java)
        startActivity(intent)
    }
}