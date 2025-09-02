package com.example.quizos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizos.R
import com.example.quizos.data.AuthRepository
import com.example.quizos.data.QuizRepository
import com.example.quizos.view.Category
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.quizos.model.User
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import android.net.Uri


data class ProfileUiState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val user: User? = null,
    val rank: Int = 0,
)

class ProfileViewModel(private val quizRepository: QuizRepository, private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState
    private val _logoutEvent = MutableSharedFlow<Unit>()
    val logoutEvent = _logoutEvent.asSharedFlow()

    init {
        loadProfileData()
    }

     fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val userResult = authRepository.getCurrentUserDetails()
            val user = userResult.getOrNull()
            if (user == null) {
                _uiState.value = ProfileUiState(isLoading = false, error = userResult.exceptionOrNull()?.message)
                return@launch
            }

            val rankResult = authRepository.getUserRank(user)
            val rank = rankResult.getOrNull() ?: 0

            // Then fetch category counts
            val categoryResult = quizRepository.getCategoryCounts()
            categoryResult.fold(
                onSuccess = { counts ->
                    val totalQuestions = counts.values.sum()
                    val categoryList = mutableListOf<Category>()

                    categoryList.add(Category("Mixed", "$totalQuestions questions", R.drawable.shuffle_icon))
                    counts.forEach { (name, count) ->
                        categoryList.add(Category(name, "$count questions", getIconForCategory(name)))
                    }

                    _uiState.value = ProfileUiState(user = user, categories = categoryList, isLoading = false, rank = rank)
                },
                onFailure = { exception ->
                    _uiState.value = ProfileUiState(user = user, isLoading = false, error = exception.message)
                }
            )
        }
    }

    fun addCoins(amount: Int) {
        val currentUser = _uiState.value.user ?: return

        viewModelScope.launch {
            val newTotalCoins = currentUser.coins + amount
            val result = authRepository.updateUserCoins(newTotalCoins)
            if (result.isSuccess) {
                // Refresh the data to show the new coin amount
                loadProfileData()
            } else {
                // Optionally handle the error
                _uiState.value = _uiState.value.copy(error = result.exceptionOrNull()?.message)
            }
        }
    }

    fun logout() {
        authRepository.logout()
        viewModelScope.launch {
            _logoutEvent.emit(Unit)
        }
    }

    private fun getIconForCategory(categoryName: String): Int {
        return when (categoryName) {
            "History" -> R.drawable.history_icon
            "Geography" -> R.drawable.earth_icon
            "Literature" -> R.drawable.literature_icon
            "Science" -> R.drawable.science_icon
            "Pop culture" -> R.drawable.movies_icon
            "Sports" -> R.drawable.sports_icon
            else -> R.drawable.shuffle_icon
        }
    }
}

class ProfileViewModelFactory(private val quizRepository: QuizRepository,     private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(quizRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}