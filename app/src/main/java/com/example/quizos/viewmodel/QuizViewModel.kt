package com.example.quizos.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.quizos.data.QuizRepository
import com.example.quizos.model.Option
import com.example.quizos.model.QuestionCard
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.quizos.data.AuthRepository


data class QuizUiState(
    val questions: List<QuestionCard> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val selectedOption: Option? = null,
    val score: Int = 0,
    val lives: Int = 3,
    val coins: Int = 50,
    val timeRemaining: Int = 20,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isQuizOver: Boolean = false,
    val hiddenOptions: List<String> = emptyList()

) {
    val currentQuestion: QuestionCard?
        get() = questions.getOrNull(currentQuestionIndex)
}
private const val FIFTY_FIFTY_CHEAT_COST = 10
private const val ADD_TIME_CHEAT_COST = 15
private const val SKIP_CHEAT_COST = 20
private const val AUDIENCE_CHEAT_COST = 25
private const val INITIAL_TIME = 20

class QuizViewModel(
    private val quizRepository: QuizRepository,
    private val categoryName: String,
    private val authRepository: AuthRepository,
    private val notificationViewModel: NotificationViewModel,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuizUiState())
    val uiState: StateFlow<QuizUiState> = _uiState

    private var timerJob: Job? = null

    init {
        loadQuestions()
 
    }

    private fun loadQuestions() {
        viewModelScope.launch {
            _uiState.value = QuizUiState(isLoading = true)
            val userResult = authRepository.getCurrentUserDetails()
            val initialCoins = userResult.getOrNull()?.coins ?: 50

            val result = quizRepository.getQuestionsForCategory(categoryName)
            result.fold(
                onSuccess = { questions ->
                    _uiState.value = QuizUiState(
                        questions = questions,
                        isLoading = false,
                        coins = initialCoins
                    )
                    startTimer()
                },
                onFailure = { exception ->
                    _uiState.value = QuizUiState(
                        isLoading = false,
                        error = exception.message,
                        coins = initialCoins
                    )
                }
            )
        }
    }

    fun onOptionSelected(option: Option) {
        if (_uiState.value.selectedOption != null) return

        timerJob?.cancel()
        var newScore = _uiState.value.score
        var newLives = _uiState.value.lives

        if (option.correctAnswer) {
            newScore += _uiState.value.timeRemaining
        } else {
            newLives -= 1
            newScore = (newScore - 10).coerceAtLeast(0)
        }

        _uiState.value = _uiState.value.copy(
            selectedOption = option,
            score = newScore,
            lives = newLives
        )

        moveToNextQuestionWithDelay()
    }

    private fun onTimeExpired() {
        if (_uiState.value.selectedOption != null) return

        val newLives = _uiState.value.lives - 1
        val newScore = (_uiState.value.score - 10).coerceAtLeast(0)

        _uiState.value = _uiState.value.copy(
            lives = newLives,
            score = newScore,
            selectedOption = null
        )

        moveToNextQuestionWithDelay()
    }

    private fun moveToNextQuestionWithDelay() {
       viewModelScope.launch {
            delay(2000)
            moveToNextQuestion()
        }
    }

    private fun moveToNextQuestion() {
        val nextIndex = _uiState.value.currentQuestionIndex + 1
        if (nextIndex < _uiState.value.questions.size && _uiState.value.lives > 0) {
            _uiState.value = _uiState.value.copy(
                currentQuestionIndex = nextIndex,
                selectedOption = null,
                timeRemaining = INITIAL_TIME,
                hiddenOptions = emptyList()
            )
            startTimer()
        } else {
            // Quiz is over
            timerJob?.cancel()
            _uiState.value = _uiState.value.copy(isQuizOver = true)
             saveFinalScore()
        }
    }
    

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0) {
                delay(1000)
                _uiState.value = _uiState.value.copy(timeRemaining = _uiState.value.timeRemaining - 1)
            }
            onTimeExpired()
        }
    }

    private fun saveFinalScore() {
        viewModelScope.launch {
            val userResult = authRepository.getCurrentUserDetails()
            if (userResult.isSuccess) {
                val currentUser = userResult.getOrThrow()
                val finalQuizScore = _uiState.value.score
                val finalCoins = _uiState.value.coins

                val newTotalPoints = currentUser.points + finalQuizScore
                authRepository.updateUserStats(newTotalPoints, finalCoins)
                val isHighest = authRepository.isNewHighScore(newTotalPoints)
                if (isHighest.getOrNull() == true) {
                    notificationViewModel.showNewHighScoreNotification()
                }
            }

        }
    }

   fun useFiftyFiftyCheat() {
        val state = _uiState.value
        if (state.selectedOption != null || state.coins < FIFTY_FIFTY_CHEAT_COST || state.hiddenOptions.isNotEmpty()) return

        val currentQuestion = state.currentQuestion ?: return
        val incorrectOptions = currentQuestion.options.filter { !it.correctAnswer }
        val optionsToRemoveCount = (currentQuestion.options.size / 2).coerceAtMost(incorrectOptions.size)
        val optionsToHide = incorrectOptions.shuffled().take(optionsToRemoveCount)

        _uiState.value = state.copy(
            coins = state.coins - FIFTY_FIFTY_CHEAT_COST,
            hiddenOptions = optionsToHide.map { it.value }
        )
    }

    fun useAudienceCheat() {
        val state = _uiState.value
        if (state.selectedOption != null || state.coins < AUDIENCE_CHEAT_COST) return

        _uiState.value = state.copy(coins = state.coins - AUDIENCE_CHEAT_COST)
        val correctOption = state.currentQuestion?.options?.find { it.correctAnswer }
        if (correctOption != null) {
            onOptionSelected(correctOption)
        }
    }

    fun useAddTimeCheat() {
        val state = _uiState.value
        if (state.selectedOption != null || state.coins < ADD_TIME_CHEAT_COST) return

        _uiState.value = state.copy(
            coins = state.coins - ADD_TIME_CHEAT_COST,
            timeRemaining = INITIAL_TIME
        )
    }

    fun useSkipCheat() {
        val state = _uiState.value
        if (state.selectedOption != null || state.coins < SKIP_CHEAT_COST) return

        timerJob?.cancel()
        _uiState.value = state.copy(coins = state.coins - SKIP_CHEAT_COST)
        moveToNextQuestion()
    }
}

class QuizViewModelFactory(
    private val quizRepository: QuizRepository,
    private val categoryName: String,
    private val authRepository: AuthRepository,
    private val notificationViewModel: NotificationViewModel,


) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuizViewModel::class.java)) {
            return QuizViewModel(quizRepository, categoryName, authRepository, notificationViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}