package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CelebrationType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class SparkParticle(
    val startX: Float,
    val startY: Float,
    val angle: Double,
    val speed: Float,
    val color: Color,
    val radius: Float
)

@Composable
fun CelebrationOverlay(
    celebration: CelebrationType?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (celebration == null) return

    val progress = remember { Animatable(0f) }

    // Generate random firecracker spark particles
    val particles = remember(celebration) {
        val colors = when (celebration) {
            CelebrationType.FOUR -> listOf(
                Color(0xFF2563EB), Color(0xFF60A5FA), Color(0xFFFBBF24), Color.White, Color(0xFF38BDF8)
            )
            CelebrationType.SIX -> listOf(
                Color(0xFF16A34A), Color(0xFF4ADE80), Color(0xFFF59E0B), Color(0xFFFDE047), Color.White
            )
            CelebrationType.WICKET -> listOf(
                Color(0xFFDC2626), Color(0xFFEF4444), Color(0xFFF97316), Color(0xFFFBBF24), Color.White
            )
        }

        List(50) {
            SparkParticle(
                startX = 0.5f,
                startY = 0.45f,
                angle = Random.nextDouble(0.0, Math.PI * 2),
                speed = Random.nextFloat() * 320f + 120f,
                color = colors[it % colors.size],
                radius = Random.nextFloat() * 6f + 3f
            )
        }
    }

    LaunchedEffect(celebration) {
        progress.snapTo(0f)
        launch {
            progress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
            )
        }
        delay(2500)
        onDismiss()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable { onDismiss() }
            .testTag("celebration_overlay"),
        contentAlignment = Alignment.Center
    ) {
        // Firecrackers Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val currentProgress = progress.value
            val alpha = (1f - currentProgress).coerceIn(0f, 1f)

            particles.forEach { p ->
                val distance = p.speed * currentProgress
                val x = (p.startX * width) + (cos(p.angle) * distance).toFloat()
                // Add gentle downward gravity
                val y = (p.startY * height) + (sin(p.angle) * distance).toFloat() + (currentProgress * currentProgress * 80f)

                drawCircle(
                    color = p.color.copy(alpha = alpha),
                    radius = p.radius * (1f - currentProgress * 0.5f),
                    center = Offset(x, y)
                )
            }
        }

        // Hero Celebration Card
        AnimatedVisibility(
            visible = true,
            enter = scaleIn(animationSpec = tween(350, easing = FastOutSlowInEasing)) + fadeIn(),
            exit = scaleOut() + fadeOut()
        ) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color(celebration.colorHex)),
                modifier = Modifier
                    .padding(32.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(24.dp))
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color(celebration.colorHex),
                                    Color(celebration.colorHex).copy(alpha = 0.85f)
                                )
                            )
                        )
                        .padding(horizontal = 28.dp, vertical = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (celebration) {
                                CelebrationType.FOUR -> Icons.Default.SportsCricket
                                CelebrationType.SIX -> Icons.Default.AutoAwesome
                                CelebrationType.WICKET -> Icons.Default.LocalFireDepartment
                            },
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = celebration.title,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = celebration.subtitle,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.95f)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.25f))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "🎆 QUICK HEROES 11 🎆",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
