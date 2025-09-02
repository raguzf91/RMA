package com.example.quizos.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.quizos.R
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CircularProgressIndicator
import com.example.quizos.viewmodel.LeaderboardViewModel
import com.example.quizos.viewmodel.LeaderboardViewModelFactory
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizos.MyApplication
import com.example.quizos.model.User
import java.text.NumberFormat
import java.util.Locale
@Composable
fun LeaderboardScreen() {
    val application = LocalContext.current.applicationContext as MyApplication
    val viewModel: LeaderboardViewModel = viewModel(
        factory = LeaderboardViewModelFactory(application.container.authRepository)
    )
    val uiState by viewModel.uiState.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.leaderboard_background),
            contentDescription = "Leaderboard background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = "Leaderboard",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(12.dp))
            LeaderboardTabs()
            Spacer(modifier = Modifier.height(16.dp))
            if (uiState.isLoading) {
                CircularProgressIndicator(color = Color.White)
            } else if (uiState.error != null) {
                Text(text = "Error: ${uiState.error}", color = Color.Red)
            } else {
                val topPlayers = uiState.topPlayers
                if (topPlayers.isNotEmpty()) {
                    TopThreePlayers(topPlayers)
                }
                Spacer(modifier = Modifier.height(60.dp))

                val restOfPlayers = if (topPlayers.size > 3) topPlayers.subList(3, topPlayers.size) else emptyList()
                LazyColumn(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(restOfPlayers.size) { index ->
                        val player = restOfPlayers[index]
                        PlayerRow(
                            rank = index + 4,
                            user = player
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}

@Composable
fun LeaderboardTabs() {
    val tabs = listOf("All time", "This week", "Month")
    var selectedIndex by remember { mutableStateOf(0) }

    TabRow(
        selectedTabIndex = selectedIndex,
        containerColor = Color.White,
        indicator = { tabPositions ->
            Box(
                modifier = Modifier
                    .tabIndicatorOffset(tabPositions[selectedIndex])
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF513d80),
                                Color(0xFF513d80).copy(alpha = 0.8f)
                            )
                        ),
                        shape = RoundedCornerShape(50)
                    )
            )
        },
        divider = {},
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(40.dp)
            .clip(RoundedCornerShape(50))
    ) {
        tabs.forEachIndexed { index, title ->
            Tab(
                selected = selectedIndex == index,
                onClick = { selectedIndex = index },
                text = {
                    Text(
                        text = title,
                        color = if (selectedIndex == index) Color.White else Color.Gray,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                interactionSource = remember { MutableInteractionSource() },
            )
        }
    }
}

@Composable
fun TopThreePlayers(players: List<User>) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom
    ) {
        // 2nd Place Player
        if (players.size >= 2) {
            val player2 = players[1]
            PlayerPodiumView(
                rank = 2,
                name = player2.username,
                score = formatter.format(player2.points),
                avatarRes = R.drawable.default_profile_photo,
                modifier = Modifier.padding(top = 40.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(110.dp))
        }

        // 1st Place Player
        if (players.isNotEmpty()) {
            val player1 = players[0]
            PlayerPodiumView(
                rank = 1,
                name = player1.username,
                score = formatter.format(player1.points),
                avatarRes = R.drawable.default_profile_photo
            )
        }

        if (players.size >= 3) {
            val player3 = players[2]
            PlayerPodiumView(
                rank = 3,
                name = player3.username,
                score = formatter.format(player3.points),
                avatarRes = R.drawable.default_profile_photo,
                modifier = Modifier.padding(top = 40.dp)
            )
        } else {
            Spacer(modifier = Modifier.width(110.dp))
        }
    }
}

@Composable
fun PlayerPodiumView(
    rank: Int,
    name: String,
    score: String,
    avatarRes: Int,
    modifier: Modifier = Modifier
) {
    val isTopPlayer = rank == 1
    val avatarSize = if (isTopPlayer) 100.dp else 80.dp
    val badgeSize = if (isTopPlayer) 28.dp else 24.dp
    val badgeOffset = if (isTopPlayer) 12.dp else 10.dp
    val nameFontSize = if (isTopPlayer) 16.sp else 16.sp
    val scoreFontSize = if (isTopPlayer) 16.sp else 14.sp

    val (borderColor, badgeColor) = when (rank) {
        1 -> Color(0xFFFFD700) to Color(0xFFFFD700)
        2 -> Color(0xFFC0C0C0) to Color(0xFFC0C0C0)
        else -> Color(0xFFCD7F32) to Color(0xFFCD7F32)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.width(110.dp)
    ) {
        if (isTopPlayer) {
            Image(
                painter = painterResource(id = R.drawable.crown_icon),
                contentDescription = "Crown",
                modifier = Modifier.size(60.dp)
            )
        }
        Box(
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = avatarRes),
                contentDescription = "Player Avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(avatarSize)
                    .clip(CircleShape)
                    .border(4.dp, borderColor, CircleShape)
            )

            Box(
                modifier = Modifier
                    .offset(y = badgeOffset)
                    .size(badgeSize)
                    .background(badgeColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = rank.toString(),
                    color = Color.DarkGray,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = name,
            fontSize = nameFontSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Text(
            text = score,
            fontSize = scoreFontSize,
            color = Color.White.copy(alpha = 0.8f)
        )
    }
}

@Composable
fun PlayerRow(rank: Int, user: User) {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.Black.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = rank.toString(),
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.width(30.dp)
            )
            Image(
                painter = painterResource(id = R.drawable.default_profile_photo),
                contentDescription = "${user.username}'s avatar",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = user.username,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = formatter.format(user.points),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF29B6F6)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LeaderboardScreenPreview() {
    LeaderboardScreen()
}