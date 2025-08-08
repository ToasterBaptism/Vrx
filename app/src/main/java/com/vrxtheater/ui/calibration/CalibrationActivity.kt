package com.vrxtheater.ui.calibration

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.vrxtheater.ui.theme.VrxTheaterTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalibrationActivity : AppCompatActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            VrxTheaterTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalibrationScreen(
                        onNavigateUp = { finish() }
                    )
                }
            }
        }
    }
}