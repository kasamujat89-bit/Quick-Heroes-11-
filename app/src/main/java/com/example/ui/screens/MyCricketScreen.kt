package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchStatus
import com.example.data.SampleData
import com.example.ui.theme.CricCardLight
import com.example.ui.theme.CricGreen
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTealLight
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel

@Composable
fun MyCricketScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val allMatches by viewModel.matchesList.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) } // 0: My Matches, 1: My Teams, 2: Tournaments

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("my_cricket_screen")
    ) {
        // Top Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                Text(
                    text = "My Cricket (મારું ક્રિકેટ)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Manage your matches, teams, scoring and tournament records",
                    fontSize = 12.sp,
                    color = CricTextSecondary
                )
            }
        }

        // Tabs Row: Matches, Teams, Tournaments
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = CricTeal
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Matches", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Teams", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Tournaments", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        // Scrollable Tab Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // HERO CARD: START A NEW MATCH
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp)),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        listOf(Color(0xFF0F766E), Color(0xFF0D9488), Color(0xFF14B8A6))
                                    )
                                )
                                .padding(18.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.SportsCricket, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Start a New Match",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "ટીમના નામ, ખેલાડીઓ ઉમેરો, ટોસ કરો અને લાઈવ સ્કોરિંગ શરૂ કરો.",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.MatchSetup) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.testTag("start_match_hero_button")
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, tint = CricTeal, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("+ Start Match / Go Live", color = CricTeal, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // ACTIVE LIVE MATCH (IF ANY)
                    val liveMatch = currentMatch
                    if (liveMatch != null && liveMatch.status == MatchStatus.LIVE) {
                        Text(
                            text = "Live / Ongoing Match",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LiveMatchPreviewCard(
                            match = liveMatch,
                            onWatchMatch = { viewModel.openMatchAsSpectator(liveMatch) },
                            onScoreMatch = { viewModel.openMatchAsScorer(liveMatch) },
                            onViewScorecard = { viewModel.navigateTo(AppScreen.FullScorecard) }
                        )
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // COMPLETED MATCHES
                    Text(
                        text = "Recent Matches",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    allMatches.forEach { match ->
                        MatchSummaryRow(
                            match = match,
                            onClick = {
                                if (match.status == MatchStatus.LIVE) {
                                    viewModel.openMatchAsSpectator(match)
                                } else {
                                    viewModel.navigateTo(AppScreen.FullScorecard)
                                }
                            }
                        )
                    }
                }

                1 -> {
                    // TEAMS OVERVIEW
                    Text("My Teams & Squads", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))

                    listOf(SampleData.teamNaliya, SampleData.teamKutch).forEach { team ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(Color(team.colorHex)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(team.shortName, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(team.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(team.city, fontSize = 12.sp, color = CricTextSecondary)
                                        }
                                    }
                                    Text("${team.players.size} Players", fontSize = 12.sp, color = CricTeal, fontWeight = FontWeight.SemiBold)
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Players: " + team.players.joinToString(", ") { it.name },
                                    fontSize = 12.sp,
                                    color = CricTextSecondary,
                                    maxLines = 2
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.MatchSetup) },
                        colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Create New Team & Match", fontWeight = FontWeight.Bold)
                    }
                }

                2 -> {
                    // TOURNAMENTS
                    SampleData.tournaments.forEach { t ->
                        TournamentCardItem(tournament = t)
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }
    }
}
