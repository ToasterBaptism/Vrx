package com.vrxtheater.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vrxtheater.data.models.DeviceCapabilities
import com.vrxtheater.data.models.GameInfo
import com.vrxtheater.data.repository.DeviceCapabilitiesRepository
import com.vrxtheater.data.repository.GameRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val deviceCapabilitiesRepository: DeviceCapabilitiesRepository,
    private val gameRepository: GameRepository
) : ViewModel() {
    
    // Device capabilities
    val deviceCapabilities: StateFlow<DeviceCapabilities> = deviceCapabilitiesRepository.deviceCapabilities
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DeviceCapabilities()
        )
    
    // Recent games (top 5)
    val recentGames: StateFlow<List<GameInfo>> = gameRepository.games
        .map { games -> games.sortedByDescending { it.lastUsedDate }.take(5) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    
    // Loading state
    val isLoading: StateFlow<Boolean> = gameRepository.isLoading
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )
    
    init {
        // Refresh device capabilities
        deviceCapabilitiesRepository.refreshDeviceCapabilities()
        
        // Load games
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