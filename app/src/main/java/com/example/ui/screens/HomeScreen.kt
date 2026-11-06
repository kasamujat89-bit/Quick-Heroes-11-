package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CricketMatch
import com.example.data.MatchStatus
import com.example.ui.components.CricHelmetAvatar
import com.example.ui.theme.CricCardLight
import com.example.ui.theme.CricDarkCard
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricGreen
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel

@Composable
fun HomeScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val currentMatch by viewModel.currentMatch.collectAsState()
    val allMatches by viewModel.matchesList.collectAsState()
    val players by viewModel.players.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
            .testTag("home_screen")
    ) {
        // Search Bar (Matching Screenshot 1)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .clickable { viewModel.navigateTo(AppScreen.FindCricketers) }
                .testTag("home_search_bar"),
            placeholder = {
                Text("Search cricketers, teams, matches", color = CricTextSecondary, fontSize = 14.sp)
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = CricTextSecondary)
            },
            shape = RoundedCornerShape(24.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE2E8F0),
                focusedBorderColor = CricTeal,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        // Stories / Quick Network Follow Row (Matching Screenshot 1)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Story with "+"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.FindCricketers) }
            ) {
                Box(contentAlignment = Alignment.BottomEnd) {
                    CricHelmetAvatar(sizeDp = 52)
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(CricTeal)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("My Network", fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Medium)
            }

            // Quick Follow items
            players.take(4).forEach { player ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.FindCricketers) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0))
                            .border(1.5.dp, if (player.isPro) CricTeal else Color.Transparent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = player.name.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = CricTextPrimary,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = player.name.split(" ").firstOrNull() ?: player.name,
                        fontSize = 11.sp,
                        color = CricTextSecondary,
                        maxLines = 1
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Red Hero Card: "Matches of your network" (Matching Screenshot 1)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .shadow(6.dp, RoundedCornerShape(16.dp))
                .testTag("hero_matches_card"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFE11D48), Color(0xFFBE123C), Color(0xFF9F1239))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Matches of your network",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Follow friends, teams and tournaments to see their matches here.",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(AppScreen.FindCricketers) },
                        colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("start_following_button")
                    ) {
                        Text("Start following", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Live Scoring Quick Action Banner
        val activeMatch = currentMatch
        if (activeMatch != null && activeMatch.status == MatchStatus.LIVE) {
            Spacer(modifier = Modifier.height(10.dp))
            LiveMatchPreviewCard(
                match = activeMatch,
                onWatchMatch = { viewModel.openMatchAsSpectator(activeMatch) },
                onScoreMatch = { viewModel.openMatchAsScorer(activeMatch) },
                onViewScorecard = { viewModel.navigateTo(AppScreen.FullScorecard) }
            )
        } else {
            Spacer(modifier = Modifier.height(10.dp))
            StartMatchHomePromptCard(
                onStartMatch = { viewModel.navigateTo(AppScreen.MatchSetup) }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Feature Grid (Start Match, Find Cricketers, Tournaments, Looking)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            QuickFeatureItem(
                icon = Icons.Default.SportsCricket,
                title = "Start Match",
                subtitle = "New scoring",
                color = CricTeal,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(AppScreen.MatchSetup) }
            )
            QuickFeatureItem(
                icon = Icons.Default.EmojiEvents,
                title = "Tournaments",
                subtitle = "100% Free Entry",
                color = CricGold,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(AppScreen.Tournaments) }
            )
            QuickFeatureItem(
                icon = Icons.Default.Groups,
                title = "Community",
                subtitle = "315+ Cricketers",
                color = CricSecondary,
                modifier = Modifier.weight(1f),
                onClick = { viewModel.navigateTo(AppScreen.FindCricketers) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Matches Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Matches",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (allMatches.isNotEmpty()) {
                Text(
                    text = "See all",
                    color = CricTeal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(AppScreen.FullScorecard) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (allMatches.isEmpty()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.SportsCricket,
                        contentDescription = null,
                        tint = CricTeal,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No matches played yet",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap 'Start Match' button to begin a new match.",
                        fontSize = 12.sp,
                        color = CricTextSecondary
                    )
                }
            }
        } else {
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
    }
}

@Composable
fun LiveMatchPreviewCard(
    match: CricketMatch,
    onWatchMatch: () -> Unit,
    onScoreMatch: () -> Unit,
    onViewScorecard: () -> Unit
) {
    val inning = match.currentInning
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .testTag("live_match_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Live badge and Tournament Title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CricSecondary)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "LIVE",
                        color = CricSecondary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "${match.totalOvers} Overs | ${match.ballType}",
                    color = CricTextSecondary,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teams & Live Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = inning?.battingTeam?.name ?: match.teamA.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "vs ${inning?.bowlingTeam?.name ?: match.teamB.name}",
                        fontSize = 13.sp,
                        color = CricTextSecondary
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${inning?.totalRuns ?: 0}/${inning?.wickets ?: 0}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = CricSecondary
                    )
                    Text(
                        text = "(${inning?.oversString ?: "0.0"}/${match.totalOvers} ov)",
                        fontSize = 13.sp,
                        color = CricTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Current batsmen on crease
            if (inning != null && inning.striker != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "*${inning.striker?.player?.name ?: ""}: ${inning.striker?.runs ?: 0} (${inning.striker?.balls ?: 0})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "CRR: ${String.format("%.2f", inning.runRate)}",
                        fontSize = 12.sp,
                        color = CricTeal,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons: Spectator View (No keypad) vs Scorer Mode (Keypad) vs Scorecard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Spectator View Button
                Button(
                    onClick = onWatchMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.1f).testTag("spectate_live_match_button")
                ) {
                    Text("🔴 Watch Live", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                // Scorer Mode Button
                Button(
                    onClick = onScoreMatch,
                    colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.1f).testTag("live_score_button")
                ) {
                    Icon(Icons.Default.SportsCricket, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Score Match", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                // Scorecard Button
                Button(
                    onClick = onViewScorecard,
                    colors = ButtonDefaults.outlinedButtonColors(),
                    border = CardDefaults.outlinedCardBorder(),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(0.9f).testTag("view_scorecard_button")
                ) {
                    Text("Scorecard", color = MaterialTheme.colorScheme.onSurface, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun QuickFeatureItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
            Text(subtitle, fontSize = 11.sp, color = CricTextSecondary, maxLines = 1)
        }
    }
}

@Composable
fun MatchSummaryRow(
    match: CricketMatch,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = match.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = match.resultMessage ?: "${match.teamA.shortName} vs ${match.teamB.shortName}",
                    fontSize = 12.sp,
                    color = if (match.status == MatchStatus.LIVE) CricSecondary else CricTextSecondary
                )
            }
            Surface(
                color = if (match.status == MatchStatus.LIVE) Color(0xFFFFE4E6) else Color(0xFFF1F5F9),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = if (match.status == MatchStatus.LIVE) "LIVE" else "RESULT",
                    color = if (match.status == MatchStatus.LIVE) CricSecondary else CricTextSecondary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun StartMatchHomePromptCard(
    onStartMatch: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onStartMatch() }
            .testTag("start_match_prompt_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(CricTeal)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "NEW MATCH",
                        color = CricTeal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Start a Cricket Match",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Add teams and players to start live scoring",
                    fontSize = 12.sp,
                    color = CricTextSecondary
                )
            }

            Button(
                onClick = onStartMatch,
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(38.dp)
            ) {
                Icon(Icons.Default.SportsCricket, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Start", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
