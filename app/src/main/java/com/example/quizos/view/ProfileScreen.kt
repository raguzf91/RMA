package com.example.quizos.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(text = "User Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        StatCard(label = "Correct Answers", value = "45")
        StatCard(label = "Incorrect Answers", value = "12")
        StatCard(label = "Avg. Time", value = "8s")

        Text(text = "Favorite Questions", fontSize = 20.sp, fontWeight = FontWeight.Medium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("• What is Kotlin used for?")
            Text("• Android Compose basics?")
        }
    }
}