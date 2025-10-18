package com.example.GaoDe.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class MapUiState {
    object Loading : MapUiState()
    object Success : MapUiState()
    data class Error(val message: String) : MapUiState()
}

class HomePageViewModel : ViewModel() {
    private val _uiState = MutableStateFlow<MapUiState>(MapUiState.Loading)
    val uiState: StateFlow<MapUiState> = _uiState

    init {
        loadMap()
    }

    private fun loadMap() {
        viewModelScope.launch {
            _uiState.value = MapUiState.Loading
            try {
                delay(1500L)
                _uiState.value = MapUiState.Success
            } catch (e: Exception) {
                _uiState.value = MapUiState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun retryLoadMap() {
        loadMap()
    }
}