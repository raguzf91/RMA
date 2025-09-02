package com.example.quizos

import android.app.Application
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
import com.example.quizos.view.HomeScreen
import com.example.quizos.view.LeaderboardScreen
import com.example.quizos.view.QuizScreen
import com.example.quizos.view.ProfileScreen 
import com.example.quizos.view.TutorialScreen
import com.example.quizos.view.LoginScreen
import com.example.quizos.view.RegisterScreen
import com.google.firebase.FirebaseApp
import com.example.quizos.model.User
import androidx.navigation.NavType
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

object AppDestinations {
    const val HOME_ROUTE = "home"
    const val QUIZ_ROUTE = "quiz/{categoryName}"
    const val LEADERBOARD_ROUTE = "leaderboard"
    const val PROFILE_ROUTE = "profile" // Base route
    const val PROFILE_ROUTE_WITH_ARGS = "profile/{username}" // Route with arguments
    const val TUTORIAL_ROUTE = "tutorial"
    const val LOGIN_ROUTE = "login"
    const val REGISTER_ROUTE = "register"

}


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
                    NavHost(navController = navController, startDestination = AppDestinations.HOME_ROUTE) {
                        composable(AppDestinations.HOME_ROUTE) {
                        HomeScreen(

                                onLogin = { navController.navigate(AppDestinations.LOGIN_ROUTE) },
                                onRegister = { navController.navigate(AppDestinations.REGISTER_ROUTE) }
                        )
                        }
                        composable(
                            route = AppDestinations.QUIZ_ROUTE,
                            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val categoryName = backStackEntry.arguments?.getString("categoryName")
                            if (categoryName != null) {
                                QuizScreen(categoryName = categoryName, onGoToProfile = { navController.popBackStack() })
                            }
                        }
                        composable(AppDestinations.LEADERBOARD_ROUTE) { LeaderboardScreen() }
                         composable(
                            route = AppDestinations.PROFILE_ROUTE_WITH_ARGS,
                            arguments = listOf(navArgument("username") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val username = backStackEntry.arguments?.getString("username") ?: "Guest"
                             ProfileScreen(
                                username = username,
                                onNavigateToLeaderboard = { navController.navigate(AppDestinations.LEADERBOARD_ROUTE) },
                                onCategoryClick = { categoryName ->
                                    navController.navigate("quiz/$categoryName")
                                },
                                onLogoutSuccess = {
                                    navController.navigate(navController.graph.startDestinationId) {
                                        popUpTo(AppDestinations.HOME_ROUTE) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                    
                                }
                            )
                        }
                        composable(AppDestinations.TUTORIAL_ROUTE) {
                        TutorialScreen(onReturnHome = { navController.navigate(AppDestinations.HOME_ROUTE)})
                        }
                        composable(AppDestinations.LOGIN_ROUTE) {
                             LoginScreen(
                                onLoginSuccess = { user ->
                                    navController.navigate("${AppDestinations.PROFILE_ROUTE}/${user.username}") {
                                        popUpTo(AppDestinations.HOME_ROUTE) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable(AppDestinations.REGISTER_ROUTE) {
                            RegisterScreen(
                                onRegisterSuccess = {
                                    navController.navigate(AppDestinations.HOME_ROUTE) {
                                        popUpTo(AppDestinations.HOME_ROUTE) {
                                            inclusive = true
                                        }
                                    }
                                },
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}