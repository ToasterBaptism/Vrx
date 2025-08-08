package com.vrxtheater.ui.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.BugReport
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.vrxtheater.R
import com.vrxtheater.ui.games.GamesScreen
import com.vrxtheater.ui.theme.VrxTheaterTheme

@Composable
fun MainScreen(
    onNavigateToVr: (String?) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToCalibration: () -> Unit,
    onNavigateToDiagnostics: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_home)) },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Games, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_games)) },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Tune, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_calibration)) },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Filled.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_settings)) },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Outlined.BugReport, contentDescription = null) },
                    label = { Text(stringResource(R.string.nav_diagnostics)) },
                    selected = selectedTab == 4,
                    onClick = { selectedTab = 4 }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTab) {
                0 -> HomeScreen(
                    onEnterVr = { onNavigateToVr(null) }
                )
                1 -> GamesScreen(
                    onGameSelected = { packageName -> onNavigateToVr(packageName) }
                )
                2 -> {
                    // Navigate to calibration activity
                    onNavigateToCalibration()
                    selectedTab = 0 // Reset to home tab
                }
                3 -> {
                    // Navigate to settings activity
                    onNavigateToSettings()
                    selectedTab = 0 // Reset to home tab
                }
                4 -> {
                    // Navigate to diagnostics activity
                    onNavigateToDiagnostics()
                    selectedTab = 0 // Reset to home tab
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    onEnterVr: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        HomeContent(
            onEnterVr = onEnterVr
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    VrxTheaterTheme {
        MainScreen(
            onNavigateToVr = {},
            onNavigateToSettings = {},
            onNavigateToCalibration = {},
            onNavigateToDiagnostics = {}
        )
    }
}