package com.vrxtheater.ui.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Videogame
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.vrxtheater.R
import com.vrxtheater.ui.theme.VrxTheaterTheme

@Composable
fun HomeContent(
    viewModel: HomeViewModel = hiltViewModel(),
    onEnterVr: () -> Unit
) {
    val deviceCapabilities by viewModel.deviceCapabilities.collectAsState()
    val isVrCapable = deviceCapabilities.isVrCapable
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App logo/icon
            Icon(
                imageVector = Icons.Filled.ViewInAr,
                contentDescription = null,
                modifier = Modifier.size(120.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // App title
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // App description
            Text(
                text = "Experience your games in a virtual theater",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // VR capability status
            if (isVrCapable) {
                // Enter VR button
                Button(
                    onClick = onEnterVr,
                    modifier = Modifier.fillMaxWidth(0.7f)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ViewInAr,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = stringResource(R.string.vr_enter))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Recent games section
                RecentGamesSection(
                    viewModel = viewModel,
                    onGameSelected = { packageName ->
                        viewModel.launchGame(packageName)
                    }
                )
            } else {
                // Device not VR capable
                DeviceNotCapableCard(
                    missingRequirements = deviceCapabilities.getMissingRequirements()
                )
            }
        }
    }
}

@Composable
fun RecentGamesSection(
    viewModel: HomeViewModel,
    onGameSelected: (String) -> Unit
) {
    val recentGames by viewModel.recentGames.collectAsState()
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Recent Games",
                style = MaterialTheme.typography.titleMedium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            if (recentGames.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No recent games",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                // Display recent games
                recentGames.forEach { game ->
                    GameItem(
                        name = game.appName,
                        icon = Icons.Filled.Videogame,
                        onClick = { onGameSelected(game.packageName) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun GameItem(
    name: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun DeviceNotCapableCard(
    missingRequirements: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your device is not VR capable",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.error
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Missing requirements:",
                style = MaterialTheme.typography.bodyMedium
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            missingRequirements.forEach { requirement ->
                Text(
                    text = "• $requirement",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeContentPreview() {
    VrxTheaterTheme {
        HomeContent(
            onEnterVr = {}
        )
    }
}