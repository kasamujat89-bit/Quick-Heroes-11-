package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.data.SampleData
import com.example.ui.components.UserProfileAvatar
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricGreen
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

@Composable
fun PerformanceScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("performance_screen")
    ) {
        // App Bar
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.testTag("performance_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "My Performance & Profile",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        }

        // Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Card Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    UserProfileAvatar(
                        photoUri = userProfile.photoUri,
                        avatarPreset = userProfile.avatarPreset,
                        sizeDp = 64
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(userProfile.name, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(color = CricTeal, shape = RoundedCornerShape(4.dp)) {
                                Text("100% FREE", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                            }
                        }
                        Text("${userProfile.title} | ${userProfile.city}", fontSize = 12.sp, color = CricTextSecondary)
                        Text("Right-hand bat | Right-arm medium", fontSize = 11.sp, color = CricTextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Career Overview Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatMetricCard("Matches", userProfile.matches.toString(), CricTeal, Modifier.weight(1f))
                StatMetricCard("Runs", userProfile.runs.toString(), CricSecondary, Modifier.weight(1f))
                StatMetricCard("Wickets", userProfile.wickets.toString(), CricGold, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Batting Analytics
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SportsCricket, contentDescription = null, tint = CricTeal, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Batting Career Stats", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Innings", fontSize = 11.sp, color = CricTextSecondary); Text("395", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        Column { Text("Batting Avg", fontSize = 11.sp, color = CricTextSecondary); Text("38.4", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        Column { Text("Strike Rate", fontSize = 11.sp, color = CricTextSecondary); Text("148.6", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CricTeal) }
                        Column { Text("High Score", fontSize = 11.sp, color = CricTextSecondary); Text("124*", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("100s", fontSize = 11.sp, color = CricTextSecondary); Text("8", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        Column { Text("50s", fontSize = 11.sp, color = CricTextSecondary); Text("48", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        Column { Text("4s", fontSize = 11.sp, color = CricTextSecondary); Text("640", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        Column { Text("6s", fontSize = 11.sp, color = CricTextSecondary); Text("312", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bowling Analytics
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.SportsCricket, contentDescription = null, tint = CricSecondary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Bowling Career Stats", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column { Text("Overs", fontSize = 11.sp, color = CricTextSecondary); Text("640.4", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        Column { Text("Wickets", fontSize = 11.sp, color = CricTextSecondary); Text("312", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CricSecondary) }
                        Column { Text("Economy", fontSize = 11.sp, color = CricTextSecondary); Text("6.82", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                        Column { Text("Best Bowling", fontSize = 11.sp, color = CricTextSecondary); Text("5/18", fontWeight = FontWeight.Bold, fontSize = 14.sp) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Awards & Badges
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Badges & Milestones", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        BadgePill("🏏 Century Maker", CricGold)
                        BadgePill("🎯 5-Wkt Haul", CricSecondary)
                        BadgePill("⚡ Super Striker", CricTeal)
                    }
                }
            }
        }
    }
}

@Composable
fun StatMetricCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = color)
            Text(label, fontSize = 11.sp, color = CricTextSecondary)
        }
    }
}

@Composable
fun BadgePill(text: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(10.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}
