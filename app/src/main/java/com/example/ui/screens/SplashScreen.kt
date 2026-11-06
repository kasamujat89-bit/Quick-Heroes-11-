package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricTeal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Splash Screen shown on application startup.
 * Displays the user's "QUICK HEROES 11" logo with high-energy cricket branding,
 * then smoothly navigates to the main screen.
 */
@Composable
fun SplashScreen(
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scale = remember { Animatable(0.75f) }
    val alpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.98f,
        targetValue = 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    LaunchedEffect(Unit) {
        launch {
            scale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
            )
        }
        launch {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 500)
            )
        }
        // Display logo briefly (1.8s) then enter application
        delay(1800)
        onFinish()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF030712),
                        Color(0xFF0B1329),
                        Color(0xFF030712)
                    )
                )
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                onFinish()
            }
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("splash_screen"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // Logo Image with gold rim accent
            Surface(
                shape = CircleShape,
                color = Color.Black,
                shadowElevation = 16.dp,
                modifier = Modifier
                    .scale(scale.value * pulseScale)
                    .alpha(alpha.value)
                    .size(220.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Quick Heroes 11 Logo",
                        modifier = Modifier
                            .size(210.dp)
                            .clip(CircleShape)
                            .testTag("splash_logo_image")
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Brand Title
            Text(
                text = "QUICK HEROES 11",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.5.sp,
                color = CricGold,
                modifier = Modifier
                    .alpha(alpha.value)
                    .testTag("splash_title_text")
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline
            Text(
                text = "Live Cricket Scoring & Match Management",
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE2E8F0),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Grassroots Cricket Scoring • 100% Free",
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(alpha.value)
            )

            Spacer(modifier = Modifier.height(36.dp))

            // Sleek progress bar
            LinearProgressIndicator(
                modifier = Modifier
                    .width(160.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .alpha(alpha.value),
                color = CricTeal,
                trackColor = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Direct Continue Button (allows user to skip instantly)
            Button(
                onClick = onFinish,
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .alpha(alpha.value)
                    .testTag("splash_start_button")
            ) {
                Icon(
                    imageVector = Icons.Default.SportsCricket,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Get Started",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
