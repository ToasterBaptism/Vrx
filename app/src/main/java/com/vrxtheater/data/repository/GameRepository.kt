package com.vrxtheater.data.repository

import com.vrxtheater.data.models.GameInfo
import com.vrxtheater.data.source.GameDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository for accessing and managing games
 */
@Singleton
class GameRepository @Inject constructor(
    private val gameDataSource: GameDataSource
) {
    private val _games = MutableStateFlow<List<GameInfo>>(emptyList())
    val games: Flow<List<GameInfo>> = _games.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: Flow<Boolean> = _isLoading.asStateFlow()
    
    /**
     * Refreshes the list of installed games
     */
    suspend fun refreshGames() {
        withContext(Dispatchers.IO) {
            _isLoading.value = true
            try {
                val installedGames = gameDataSource.getInstalledGames()
                _games.value = installedGames
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Launches a game by package name
     */
    fun launchGame(packageName: String): Boolean {
        return gameDataSource.launchGame(packageName)
    }
}