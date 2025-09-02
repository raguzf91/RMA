package com.example.quizos.view.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quizos.R

@Composable
fun AppBackground(content: @Composable ColumnScope.() -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background_homepage),
            contentDescription = "Homepage background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        val backgroundBrush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF513d80),
                Color(0xFF513d80).copy(alpha = 0.3f)
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundBrush)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            content()
        }
    }
}

@Composable
fun QuizLogo() {
    Image(
        painter = painterResource(id = R.drawable.logohomepage),
        contentDescription = "QuizOs Logo",
        modifier = Modifier
            .height(160.dp)
            .fillMaxWidth(),
    )
}