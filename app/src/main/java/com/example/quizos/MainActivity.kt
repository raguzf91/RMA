package com.example.quizos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.compose.foundation.layout.fillMaxSize
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.quizos.ui.theme.QuizOsTheme
import com.example.quizos.view.LeaderboardScreen
import com.example.quizos.view.QuizScreen
import com.example.quizos.view.ProfileScreen 


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuizOsTheme {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "quiz") {
                        composable("quiz") { QuizScreen() }
                        composable("leaderboard") { LeaderboardScreen() }
                        composable("profile") { ProfileScreen() }
                    }
                }
            }
        }
    }
}