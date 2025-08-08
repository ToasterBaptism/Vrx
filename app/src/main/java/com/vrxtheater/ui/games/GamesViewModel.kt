package com.vrxtheater.ui.games

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vrxtheater.data.models.GameInfo
import com.vrxtheater.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GamesViewModel @Inject constructor(
    private val gameRepository: GameRepository
) : ViewModel() {
    
    // Games list
    val games: StateFlow<List<GameInfo>> = gameRepository.games
    
    // Loading state
    val isLoading: StateFlow<Boolean> = gameRepository.isLoading
    
    init {
        // Load games
        refreshGames()
    }
    
    /**
     * Refreshes the list of installed games
     */
    fun refreshGames() {
        viewModelScope.launch {
            gameRepository.refreshGames()
        }
    }
    
    /**
     * Launches a game by package name
     */
    fun launchGame(packageName: String): Boolean {
        return gameRepository.launchGame(packageName)
    }
}