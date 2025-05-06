package com.example.quizos.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
// import androidx.compose.ui.R // Remove this line if present
import com.example.quizos.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.BookmarkBorder
import CircularCountdownTimer



@Composable
fun QuizScreen() {
  
    Column(
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
        // Top bar with back and bookmark
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
           
            
        }
        Spacer(modifier = Modifier.height(16.dp))
        // Question card with gradient background
        Text(
            text = "Topic",
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
        .height(400.dp),
        
        
    
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // top half: image + gradient
        Box(
            modifier = Modifier
                .weight(1f)               // half of Card height
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.football),
                contentDescription = "Quiz illustration",
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question 3 of 10",
                fontWeight = FontWeight.Medium,
                color = Color.LightGray
            )
        }
        // bottom half: question text
        Box(
            modifier = Modifier
                .weight(1f)               // remaining half
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Which soccer team won the FIFA World Cup for the first time?",
                fontSize = 24.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
    }
}
        Spacer(modifier = Modifier.height(16.dp))
        // Timer bar
        
        // Options list
        var selected by remember { mutableStateOf<String?>(null) }
       
    
        val options = listOf("A. Uruguay", "B. Brazil", "C. Italy", "D. Germany")
Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier.fillMaxWidth()
) {
    options.chunked(2).forEach { rowItems ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowItems.forEach { option ->
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (option == selected) 2.dp else 0.dp,
                            color = Color(0xFFB00753),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(if (option == selected) Color(0xFFD70966) else Color.White)
                        .selectable(
                            selected = option == selected,
                            onClick = { selected = option }
                        )
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = option == selected,
                        onClick = { selected = option }
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = option,
                        color = if (option == selected) Color.White else Color.Black
                    )
                }
            }
            // if odd number of options, fill space
            if (rowItems.size < 2) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

        Spacer(modifier = Modifier.height(16.dp))
        Row(
           verticalAlignment = Alignment.Top,
           
           modifier = Modifier
               .fillMaxWidth()
               .padding(vertical = 16.dp)
       ) {
          
           Spacer(modifier = Modifier.width(8.dp))
           Row(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "3 ❤️",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = "0 score",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
           
       }

       Box(
  modifier = Modifier
    .fillMaxWidth(),
  contentAlignment = Alignment.Center
) {
  CircularCountdownTimer(
    totalTimeSeconds = 60,
    modifier = Modifier.size(60.dp)
  )
}


       

        Spacer(modifier = Modifier.weight(1f))
        // Cheats buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            CheatsButton(label = "50/50", backgroundColor = Color(0xFF81C784))  // green
            CheatsButton(label = "Audience", backgroundColor = Color(0xFFF06292))// pink
            CheatsButton(label = "Add time", backgroundColor = Color(0xFF64B5F6))// blue
            CheatsButton(label = "Skip", backgroundColor = Color(0xFFE57373))    // red
        }
    }

}

@Composable
fun RowScope.CheatsButton(label: String, backgroundColor: Color ) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .padding(horizontal = 4.dp)
            .clickable { }
            
           
    ) {
        Box(
            modifier = Modifier.fillMaxSize(), // fill full card
            
            contentAlignment = Alignment.Center) {
            Text(text = label, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}
