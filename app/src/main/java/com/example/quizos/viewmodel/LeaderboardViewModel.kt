package com.example.quizos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizos.data.AuthRepository
import com.example.quizos.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class LeaderboardUiState(
    val topPlayers: List<User> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class LeaderboardViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LeaderboardUiState())
    val uiState: StateFlow<LeaderboardUiState> = _uiState

    init {
        loadLeaderboard()
    }

    private fun loadLeaderboard() {
        viewModelScope.launch {
            _uiState.value = LeaderboardUiState(isLoading = true)
            val result = authRepository.getTopUsers(10)
            result.fold(
                onSuccess = { users ->
                    _uiState.value = LeaderboardUiState(topPlayers = users, isLoading = false)
                },
                onFailure = { exception ->
                    _uiState.value = LeaderboardUiState(isLoading = false, error = exception.message)
                }
            )
        }
    }
}

class LeaderboardViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LeaderboardViewModel::class.java)) {
            return LeaderboardViewModel(authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}