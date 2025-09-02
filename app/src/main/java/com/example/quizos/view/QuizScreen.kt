package com.example.quizos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizos.MyApplication
import com.example.quizos.R
import com.example.quizos.model.Option
import com.example.quizos.ui.theme.PressStart2P
import com.example.quizos.ui.theme.QuizOsTheme
import com.example.quizos.viewmodel.QuizUiState
import com.example.quizos.viewmodel.QuizViewModel
import com.example.quizos.viewmodel.QuizViewModelFactory
@Composable
fun QuizScreen(
    categoryName: String,
    onGoToProfile: () -> Unit
) {
    val application = LocalContext.current.applicationContext as MyApplication
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModelFactory(application.container.quizRepository, categoryName, application.container.authRepository, application.container.notificationViewModel
)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF513d80),
                        Color(0xFF513d80).copy(alpha = 0.3f)
                    )
                )
            )
    ) {
        when {
            uiState.isLoading -> {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.error != null -> {
                Text(
                    text = "Error: ${uiState.error}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            uiState.isQuizOver -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Game Over",
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        fontSize = 48.sp,
                        fontFamily = PressStart2P,
                        style = TextStyle(
                            shadow = Shadow(
                                color = Color.Black.copy(alpha = 0.75f),
                                offset = Offset(8f, 8f),
                                blurRadius = 0f
                            )
                        )
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Final Score: ${uiState.score}",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontFamily = PressStart2P
                    )
                    Spacer(modifier = Modifier.height(48.dp))

                    Button(
                        onClick = onGoToProfile,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFA726))
                    ) {
                        Text(
                            text = "Return to Home",
                            fontFamily = PressStart2P,
                            fontSize = 16.sp,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        )
                    }
                }
            }
            uiState.currentQuestion != null -> {
                QuizContent(
                    uiState = uiState,
                    onOptionClick = { viewModel.onOptionSelected(it) },
                    onFiftyFiftyClick = { viewModel.useFiftyFiftyCheat() },
                    onAudienceClick = { viewModel.useAudienceCheat() },
                    onAddTimeClick = { viewModel.useAddTimeCheat() },
                    onSkipClick = { viewModel.useSkipCheat() }
                )
            }
        }
    }
}

@Composable
fun QuizContent(
    uiState: QuizUiState,
    onOptionClick: (Option) -> Unit,
    onFiftyFiftyClick: () -> Unit,
    onAudienceClick: () -> Unit,
    onAddTimeClick: () -> Unit,
    onSkipClick: () -> Unit
) {
    val currentQuestion = uiState.currentQuestion!!
    val context = LocalContext.current
    val resName = currentQuestion.topic.lowercase(java.util.Locale.ROOT)
    val imageRes = context.resources.getIdentifier(resName, "drawable", context.packageName)
        .takeIf { it != 0 } ?: R.drawable.placeholder

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = currentQuestion.topic,
                modifier = Modifier.align(Alignment.Center),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Row(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF6E4BA5))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.coin_purse),
                    contentDescription = "Coins",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${uiState.coins}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box {
                Image(
                    painter = painterResource(id = imageRes),
                    contentDescription = currentQuestion.topic,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.Crop
                )
            }
        }
               Text(
                    text = currentQuestion.question,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                )

        Spacer(modifier = Modifier.weight(1f)) // Pushes options down

        // --- Answer Options ---
        currentQuestion.options.forEach { option ->

           if (option.value !in uiState.hiddenOptions) {
                val selectedOption = uiState.selectedOption
                val isSelected = option.value == selectedOption?.value
                val showResult = selectedOption != null

                val bgColor = when {
                    isSelected && option.correctAnswer -> Color(0xFFA5D6A7)
                    isSelected && !option.correctAnswer -> Color(0xFFEF9A9A)
                    showResult && option.correctAnswer -> Color(0xFFA5D6A7)
                    else -> Color.White
                }

                val textColor = if (bgColor != Color.White) Color.Black else Color.DarkGray

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(bgColor)
                        .clickable(enabled = !showResult && uiState.timeRemaining > 0) {
                            onOptionClick(option)
                        }
                        .padding(12.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = option.value,
                        fontSize = 16.sp,
                        color = textColor
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "${uiState.lives} ❤️",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
           Box(modifier = Modifier.size(60.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { uiState.timeRemaining / 20f },
                    modifier = Modifier.fillMaxSize(),
                    color = Color(Color.Red.value),
                    strokeWidth = 4.dp,
                    trackColor = Color.White.copy(alpha = 0.3f)
                )
                Text(
                    text = "${uiState.timeRemaining}",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "${uiState.score} 👑",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CheatsButton(label = "50/50", backgroundColor = Color(0xFFFFA726), onClick = onFiftyFiftyClick, modifier = Modifier
                .weight(1f)
                .height(50.dp))
            CheatsButton(label = "Audience", backgroundColor = Color(0xFFF06292), onClick = onAudienceClick, modifier = Modifier
                .weight(1f)
                .height(50.dp))
            CheatsButton(label = "Add time", backgroundColor = Color(0xFF64B5F6), onClick = onAddTimeClick, modifier = Modifier
                .weight(1f)
                .height(50.dp))
            CheatsButton(label = "Skip", backgroundColor = Color(0xFFE57373), onClick = onSkipClick, modifier = Modifier
                .weight(1f)
                .height(50.dp))
        }
    }
}

@Composable
fun RowScope.CheatsButton(
    label: String,
    backgroundColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF513d80)
@Composable
fun QuizContentPreview() {
    val sampleQuestion = com.example.quizos.model.QuestionCard(
        topic = "Science",
        question = "What is the powerhouse of the cell?",
        options = listOf(
            Option(0, "Mitochondria", true),
            Option(1, "Nucleus", false),
            Option(2, "Ribosome", false),
            Option(3, "Endoplasmic Reticulum", false)
        )
    )
    val uiState = QuizUiState(
        isLoading = false,
        questions = listOf(sampleQuestion),
        currentQuestionIndex = 0,
        score = 120,
        lives = 2,
        coins = 35,
        timeRemaining = 15
    )
    QuizOsTheme {
        QuizContent(
            uiState = uiState,
            onOptionClick = {},
            onFiftyFiftyClick = {},
            onAudienceClick = {},
            onAddTimeClick = {},
            onSkipClick = {}
        )
    }
}