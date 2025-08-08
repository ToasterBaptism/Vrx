package com.vrxtheater.ui.games

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Videogame
import androidx.compose.material.icons.filled.ViewInAr
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import com.vrxtheater.R
import com.vrxtheater.data.models.GameInfo
import com.vrxtheater.ui.theme.VrxTheaterTheme

@Composable
fun GamesScreen(
    viewModel: GamesViewModel = hiltViewModel(),
    onGameSelected: (String) -> Unit
) {
    val games by viewModel.games.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.games_title),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )
            
            IconButton(
                onClick = { viewModel.refreshGames() }
            ) {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = stringResource(R.string.games_refresh)
                )
            }
        }
        
        // Games list
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.games_loading),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        } else if (games.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.games_empty),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn {
                items(games) { game ->
                    GameListItem(
                        game = game,
                        onGameSelected = onGameSelected
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun GameListItem(
    game: GameInfo,
    onGameSelected: (String) -> Unit
) {
    val context = LocalContext.current
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Game icon
            Box(
                modifier = Modifier.size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                game.icon?.let { icon ->
                    Image(
                        bitmap = icon.toBitmap().asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )
                } ?: Icon(
                    imageVector = Icons.Filled.Videogame,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Game info
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = game.appName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = stringResource(
                        R.string.games_last_played,
                        game.getLastPlayedFormatted()
                    ),
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Launch button
            Button(
                onClick = { onGameSelected(game.packageName) }
            ) {
                Icon(
                    imageVector = Icons.Filled.ViewInAr,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text(text = stringResource(R.string.games_launch))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamesScreenPreview() {
    VrxTheaterTheme {
        GamesScreen(
            onGameSelected = {}
        )
    }
}