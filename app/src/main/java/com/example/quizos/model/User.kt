package com.example.quizos.model

data class User(
    val uid: String = "",
    val username: String = "",
    val email: String = "",
    val points: Int = 0,
    val coins: Int = 0,
)