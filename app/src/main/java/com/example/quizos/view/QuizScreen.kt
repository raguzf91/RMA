package com.example.quizos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import com.example.quizos.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import CircularCountdownTimer
import Option
import QuestionCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

val questionCards = listOf(
    QuestionCard(
        topic = "Mathematics",
        question = "What is the derivative of sin(x)?",
        options = listOf(
            Option(index = 0, value = "cos(x)", correctAnswer = true),
            Option(index = 1, value = "-sin(x)", correctAnswer = false),
            Option(index = 2, value = "-cos(x)", correctAnswer = false),
            Option(index = 3, value = "sin(x)", correctAnswer = false)
        )
    ),
    QuestionCard(
        topic = "History",
        question = "Who was the first emperor of the Roman Empire?",
        options = listOf(
            Option(index = 0, value = "Julius Caesar", correctAnswer = false),
            Option(index = 1, value = "Augustus", correctAnswer = true),
            Option(index = 2, value = "Nero", correctAnswer = false),
            Option(index = 3, value = "Tiberius", correctAnswer = false)
        )
    ),
    QuestionCard(
        topic = "Geography",
        question = "Which country has the longest coastline in the world?",
        options = listOf(
            Option(index = 0, value = "Australia", correctAnswer = false),
            Option(index = 1, value = "Russia", correctAnswer = false),
            Option(index = 2, value = "Canada", correctAnswer = true),
            Option(index = 3, value = "United States", correctAnswer = false)
        )
    ),
    QuestionCard(
        topic = "Literature",
        question = "Who wrote ‘Pride and Prejudice’?",
        options = listOf(
            Option(index = 0, value = "Emily Brontë", correctAnswer = false),
            Option(index = 1, value = "Jane Austen", correctAnswer = true),
            Option(index = 2, value = "Charles Dickens", correctAnswer = false),
            Option(index = 3, value = "Mary Shelley", correctAnswer = false)
        )
    ),
    QuestionCard(
        topic = "Science",
        question = "What is the chemical symbol for gold?",
        options = listOf(
            Option(index = 0, value = "Ag", correctAnswer = false),
            Option(index = 1, value = "Au", correctAnswer = true),
            Option(index = 2, value = "Gd", correctAnswer = false),
            Option(index = 3, value = "Pb", correctAnswer = false)
        )
    )
)

@Composable
fun QuizScreen() {
    var currentIndex by remember { mutableStateOf(0) }
    var selectedOption by remember { mutableStateOf<Option?>(null) }
    val currentCard = questionCards[currentIndex]
    val coroutineScope = rememberCoroutineScope()
    val totalTimePerQuestion = 20
    var timeRemaining by remember { mutableStateOf(totalTimePerQuestion) }
    var score by remember { mutableStateOf(0) }
    var lives by remember { mutableStateOf(3) }
    var coins by remember { mutableStateOf(50) }
    var fiftyFiftyOptions by remember { mutableStateOf<List<Int>?>(null) }
    val context = LocalContext.current
    val resName = currentCard.topic.lowercase(java.util.Locale.ROOT)       
    val imageRes = context.resources.getIdentifier(resName, "drawable", context.packageName)
        .takeIf { it != 0 } ?: R.drawable.placeholder   
    LaunchedEffect(currentIndex) {
        timeRemaining = totalTimePerQuestion
        fiftyFiftyOptions = null
        while (timeRemaining > 0) {
          delay(1_000L)
          timeRemaining--
        }
      }


    LaunchedEffect(timeRemaining) {
        if (timeRemaining == 0 && selectedOption == null) {
          // deduct score & lives
          if (score > 0) score = (score - 10).coerceAtLeast(0)
          lives--
    
          //handle game‐over
          if (lives <= 0) {
            // TODO: dovrsi
          }
    
         
          coroutineScope.launch {
            delay(3_000L)
            currentIndex = (currentIndex + 1).coerceAtMost(questionCards.lastIndex)
            selectedOption = null
          }
        }
      }
    
   
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
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
           
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

           
            Text(
                text = currentCard.topic,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

           
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = imageRes),
                            contentDescription = currentCard.topic,
                            modifier = Modifier.matchParentSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .matchParentSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(0.4f)
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentCard.question,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

           
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                currentCard.options.forEach { option ->
                    val showCorrectOnTimeout = selectedOption == null && timeRemaining == 0
                    val show50 = fiftyFiftyOptions != null
                    val isVisible = !show50 || option.index in fiftyFiftyOptions!!
                    val bgColor = when {
                        // 1) user has tapped an option → show result
                        selectedOption != null && option == selectedOption && option.correctAnswer  -> Color(0xFFA5D6A7)  // green
                        selectedOption != null && option == selectedOption && !option.correctAnswer -> Color(0xFFEF9A9A)  // red
                        // if they picked wrong, still show the correct one
                        selectedOption != null && option.correctAnswer                           -> Color(0xFFA5D6A7)

                        // 2) timeout case
                        showCorrectOnTimeout && option.correctAnswer                             -> Color(0xFFA5D6A7)

                        // 3) 50/50 cheat (only when nothing’s picked yet)
                        show50 && option.index in fiftyFiftyOptions!!                           -> Color(0xFFFFA726)  // orange
                        show50 && !isVisible                                                     -> Color.LightGray.copy(alpha = 0.5f)

                        // 4) default un‐answered state
                        selectedOption == null                                                  -> Color.White

                        else                                                                     -> Color.White
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(bgColor)
                            .clickable(enabled = selectedOption == null && timeRemaining > 0 && isVisible) {
                                selectedOption = option
                                if(option.correctAnswer) {
                                    score = score + timeRemaining
                                } else {
                                    if ((score - 10) > 0) {
                                        score = score - 10
                                    } else {
                                        score = 0
                                    }
                                    
                                    lives = lives - 1
                                    if (lives <= 0) {
                                        //TODO dovrsi
                                    }

                                } 
                                coroutineScope.launch {
                                    delay(3000)
                                    currentIndex = (currentIndex + 1).coerceAtMost(questionCards.lastIndex)
                                    selectedOption = null
                                }
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = option.value,
                            fontSize = 18.sp,
                            color = if (bgColor == Color.White) Color.Black else Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(64.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$lives ❤️",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                CircularCountdownTimer(
                    totalTimeSeconds = totalTimePerQuestion,
                    currentTimeSeconds = timeRemaining,
                    modifier = Modifier.size(60.dp)
                )
                Text(
                    text = "$score 👑",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CheatsButton(
                    label = "50/50",
                    backgroundColor = Color(0xFFFFA726),
                    onClick = {
                      if (coins >= 25 && fiftyFiftyOptions == null) {
                        coins -= 25
                        val correctIdx = currentCard.options.first { it.correctAnswer }.index
                        val wrongs = currentCard.options.filter { !it.correctAnswer }.map { it.index }
                        fiftyFiftyOptions = listOf(correctIdx, wrongs.random())
                      }
                    },
                    modifier = Modifier.weight(1f).height(56.dp)
                  )
                CheatsButton(label = "Audience", backgroundColor = Color(0xFFF06292), modifier = Modifier.weight(1f).height(56.dp), onClick = { /* … */ })
                CheatsButton(label = "Add time", backgroundColor = Color(0xFF64B5F6), modifier = Modifier.weight(1f).height(56.dp), onClick = { /* … */ })
                CheatsButton(label = "Skip", backgroundColor = Color(0xFFE57373), modifier = Modifier.weight(1f).height(56.dp), onClick = { /* … */ })
            }
        }

        
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
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
                text = "$coins",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun RowScope.CheatsButton(
    label: String,
    backgroundColor: Color,
    onClick: ()->Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = modifier
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
