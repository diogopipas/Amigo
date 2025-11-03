package com.amigo.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.amigo.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun AmigoMascotInline(message: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Lean right + subtle wave
        val infinite = rememberInfiniteTransition(label = "amigo-inline")
        val leanProgress by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "lean"
        )
        val wave by infinite.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 600, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "wave"
        )

        val leanRotation = 6f * leanProgress
        val leanTranslateX = 4f * leanProgress
        val waveRotation = 3.5f * wave

        Image(
            painter = painterResource(id = R.drawable.amigo),
            contentDescription = "Amigo mascot",
            modifier = Modifier
                .size(64.dp)
                .padding(start = 0.dp)
                .offset(x = 0.dp)
                .graphicsLayer(
                    scaleX = -1f,
                    rotationZ = leanRotation + waveRotation,
                    translationX = leanTranslateX,
                    transformOrigin = TransformOrigin(0.5f, 0.5f)
                ),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.width(12.dp))

        AmigoSpeechBubble(text = message, modifier = Modifier.weight(1f))
    }
}

@Composable
fun AmigoMascotOverlay(message: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        // Lean right + wave animation
        val infinite = rememberInfiniteTransition(label = "amigo-overlay")
        val leanProgress by infinite.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "lean"
        )
        val wave by infinite.animateFloat(
            initialValue = -1f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 650, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "wave"
        )

        val leanRotation = 7f * leanProgress
        val leanTranslateX = 5.5f * leanProgress
        val waveRotation = 4f * wave

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.amigo),
                    contentDescription = "Amigo mascot",
                    modifier = Modifier
                        .size(84.dp)
                        .padding(start = 0.dp)
                        .offset(x = 0.dp)
                        .graphicsLayer(
                            scaleX = -1f,
                            rotationZ = leanRotation + waveRotation,
                            translationX = leanTranslateX,
                            transformOrigin = TransformOrigin(0.5f, 0.5f)
                        ),
                    contentScale = ContentScale.Fit
                )

                AmigoSpeechBubble(
                    text = message,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun AmigoSpeechBubble(text: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}


