package com.example.quizos.view

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizos.R

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun TutorialScreen(onReturnHome: () -> Unit = {}) {
    var slideCount by remember { mutableStateOf(0) }
    var sliderPosition by remember { mutableStateOf(0.2f) }
    var mascotResId by remember { mutableStateOf(R.drawable.mascot_tutorial) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(horizontal = 32.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        if (slideCount < 3) {
            mascotResId = when (slideCount) {
                0 -> R.drawable.mascot_tutorial
                1 -> R.drawable.mascot_tutorial_two
                else -> R.drawable.mascot_tutorial_three
            }
            Crossfade(targetState = mascotResId, label = "MascotCrossfade") { resourceId ->
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "Mascot"
                )
            }
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Pick a topic",
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Left
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "to start",
                fontSize = 48.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Left
            )

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    sliderPosition = newValue
                    if (newValue == 1.0f) {
                        slideCount++
                        sliderPosition = 0.2f
                    }
                    if (newValue <= 0.1f) {
                        sliderPosition = 0.1f
                    }
                },
                track = { sliderState ->
                    val trackBrush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF513d80),
                            Color(0xFF513d80).copy(alpha = 0.2f)
                        )
                    )
                    val fraction = ((sliderState.value - sliderState.valueRange.start) /
                            (sliderState.valueRange.endInclusive - sliderState.valueRange.start))
                        .coerceIn(0f, 1f)

                    Box(
                        modifier = Modifier
                            .height(52.dp)
                            .fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    color = Color.White.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(percent = 50)
                                )
                                .border(
                                    width = 2.dp,
                                    color = Color(0xFF513d80).copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        )
                        Text(
                            text = "Next",
                            color = Color.Black.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction)
                                .fillMaxHeight()
                                .background(
                                    brush = trackBrush,
                                    shape = RoundedCornerShape(percent = 50)
                                )
                        )
                    }
                },
                thumb = {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Slider Thumb",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            )
        } else {
            val gradientBrush = Brush.horizontalGradient(
                colors = listOf(Color(0xFF513d80), Color(0xFF8A59E6))
            )
            mascotResId = R.drawable.mascot_tutorial_four
            Crossfade(targetState = mascotResId, label = "MascotCrossfade") { resourceId ->
                Image(
                    painter = painterResource(id = resourceId),
                    contentDescription = "Mascot"
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                    onClick = onReturnHome,
                    shape = RoundedCornerShape(32.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
                ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(brush = gradientBrush),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "I'm ready!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                }
                }
        }
    }
}