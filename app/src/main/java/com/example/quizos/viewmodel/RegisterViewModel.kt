package com.example.quizos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizos.data.AuthDataSource
import com.example.quizos.data.AuthRepository
import com.example.quizos.data.UserDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Sealed interface to represent the different states of the UI
sealed interface RegisterUiState {
    object Idle : RegisterUiState
    object Loading : RegisterUiState
    object Success : RegisterUiState
    data class Error(val message: String) : RegisterUiState
}

class RegisterViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState

    fun registerUser(username: String, email: String, password: String) {
        if (username.isBlank() || email.isBlank() || password.length < 6) {
            _uiState.value = RegisterUiState.Error("Please fill all fields. Password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            _uiState.value = RegisterUiState.Loading

            val result = repository.registerUserAndSaveDetails(username, email, password)

            _uiState.value = result.fold(
                onSuccess = { RegisterUiState.Success },
                onFailure = { exception -> RegisterUiState.Error(exception.message ?: "An unknown error occurred.") }
            )
        }
    }
    fun resetState() {
        _uiState.value = RegisterUiState.Idle
    }
}

class RegisterViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}