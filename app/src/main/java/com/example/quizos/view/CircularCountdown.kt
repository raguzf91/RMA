import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay
import androidx.compose.foundation.layout.Box
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

@Composable
fun CircularCountdownTimer(
    totalTimeSeconds: Int,
    modifier: Modifier = Modifier,
    onFinished: () -> Unit = {}
) {
    // remember the seconds left
    var timeLeft by remember { mutableStateOf(totalTimeSeconds) }

    // animiraj krug svake sekunde
    val progressFraction by animateFloatAsState(
      targetValue = timeLeft / totalTimeSeconds.toFloat(),
      animationSpec = tween(300)
    )


    // this effect will run every second until timeLeft is 0
    LaunchedEffect(timeLeft) {
      if (timeLeft > 0) {
        delay(1_000L)
        timeLeft--
      } else {
        onFinished()
      }
    }

    Box(
      contentAlignment = Alignment.Center,
      modifier = modifier
    ) {
      // krug
      CircularProgressIndicator(
        progress = progressFraction,
        modifier = Modifier.fillMaxSize(),
        strokeWidth = 4.dp,
        color = Color.White,
        trackColor = Color.White.copy(alpha = 0.3f)
      )
      // tekst u sredini
      Text(
        text = String.format("%02d", timeLeft),
        color = Color.White,
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center
      )
    }
}
