package com.example.quizos.model
data class QuestionCard(
    val topic: String = "",
    val question: String = "",
    val options: List<Option> = emptyList()
)