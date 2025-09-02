package com.example.quizos

import android.app.Application
import android.content.Context
import com.example.quizos.data.AuthDataSource
import com.example.quizos.data.AuthRepository
import com.example.quizos.data.QuizDataSource
import com.example.quizos.data.QuizRepository
import com.example.quizos.data.UserDataSource
import com.google.firebase.FirebaseApp
import android.os.Build
import android.app.NotificationChannel
import android.app.NotificationManager
import com.example.quizos.viewmodel.NotificationViewModel

interface AppContainer {
    val authRepository: AuthRepository
    val quizRepository: QuizRepository
    val notificationViewModel: NotificationViewModel
}

class DefaultAppContainer(private val context: Context) : AppContainer {
    override val authRepository: AuthRepository by lazy {
        AuthRepository(AuthDataSource(), UserDataSource())
    }

    override val quizRepository: QuizRepository by lazy {
        QuizRepository(QuizDataSource())
    }

    override val notificationViewModel: NotificationViewModel by lazy {
        NotificationViewModel(context)
    }
}

class MyApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        container = DefaultAppContainer(this)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "High Score Notifications"
            val descriptionText = "Channel for high score alerts."
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "high_score_channel"
    }
}
