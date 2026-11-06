package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Inning
import com.example.data.SampleData
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.SportsCricket
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel

@Composable
fun FullScorecardScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentMatchState by viewModel.currentMatch.collectAsState()
    val allMatches by viewModel.matchesList.collectAsState()
    val match = currentMatchState ?: allMatches.firstOrNull()

    if (match == null) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.Default.SportsCricket,
                contentDescription = null,
                tint = CricTeal,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No Scorecard Available",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Start a new match to view detailed scorecard.",
                fontSize = 13.sp,
                color = CricTextSecondary
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.navigateTo(AppScreen.MatchSetup) },
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Start Match", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(onClick = onBack) {
                Text("Back")
            }
        }
        return
    }

    var selectedInningTab by remember { mutableIntStateOf(0) }

    val innings = match.innings
    val selectedInning = innings.getOrNull(selectedInningTab) ?: innings.firstOrNull()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("full_scorecard_screen")
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("scorecard_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Match Scorecard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "${match.teamA.name} vs ${match.teamB.name}",
                        fontSize = 12.sp,
                        color = CricTextSecondary
                    )
                }
            }
        }

        // Innings Tab Selector
        if (innings.size > 1) {
            TabRow(
                selectedTabIndex = selectedInningTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CricTeal
            ) {
                innings.forEachIndexed { index, inn ->
                    Tab(
                        selected = selectedInningTab == index,
                        onClick = { selectedInningTab = index },
                        text = {
                            Text(
                                text = "${inn.battingTeam.shortName} (${inn.totalRuns}/${inn.wickets})",
                                fontWeight = if (selectedInningTab == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }
        }

        // Scorecard Scrollable Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp)
        ) {
            if (match.resultMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CricGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = match.resultMessage!!,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E),
                            fontSize = 14.sp
                        )
                    }
                }
            }

            if (selectedInning != null) {
                InningScorecardView(inning = selectedInning)
            } else {
                Text("No innings recorded yet.", color = CricTextSecondary)
            }
        }
    }
}

@Composable
fun InningScorecardView(inning: Inning) {
    // Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${inning.battingTeam.name} - Innings ${inning.inningNumber}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "CRR: ${String.format("%.2f", inning.runRate)}",
                fontSize = 12.sp,
                color = CricTeal,
                fontWeight = FontWeight.SemiBold
            )
        }
        Text(
            text = "${inning.totalRuns}/${inning.wickets} (${inning.oversString} Ov)",
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            color = CricSecondary
        )
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Batting Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Batting", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CricTeal)
            Spacer(modifier = Modifier.height(8.dp))

            // Table header
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Batter", modifier = Modifier.weight(2.5f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("R", modifier = Modifier.weight(0.7f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("B", modifier = Modifier.weight(0.7f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("4s", modifier = Modifier.weight(0.7f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("6s", modifier = Modifier.weight(0.7f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("SR", modifier = Modifier.weight(1.1f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))

            inning.batsmenScores.forEach { b ->
                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(b.player.name, modifier = Modifier.weight(2.5f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                        Text(b.runs.toString(), modifier = Modifier.weight(0.7f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(b.balls.toString(), modifier = Modifier.weight(0.7f), fontSize = 12.sp, color = CricTextSecondary)
                        Text(b.fours.toString(), modifier = Modifier.weight(0.7f), fontSize = 12.sp, color = CricTextSecondary)
                        Text(b.sixes.toString(), modifier = Modifier.weight(0.7f), fontSize = 12.sp, color = CricTextSecondary)
                        Text(String.format("%.1f", b.strikeRate), modifier = Modifier.weight(1.1f), fontSize = 12.sp, color = CricTextSecondary)
                    }
                    Text(
                        text = b.dismissalText,
                        fontSize = 10.sp,
                        color = if (b.isOut) CricSecondary else CricTeal
                    )
                }
                HorizontalDivider(color = Color(0xFFF8FAFC))
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Extras summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Extras: ${inning.extras.total} (wd ${inning.extras.wides}, nb ${inning.extras.noBalls}, lb ${inning.extras.legByes}, b ${inning.extras.byes})",
                    fontSize = 11.sp,
                    color = CricTextSecondary
                )
                Text(
                    text = "Total: ${inning.totalRuns}/${inning.wickets}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Bowling Card
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text("Bowling", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = CricTeal)
            Spacer(modifier = Modifier.height(8.dp))

            // Table header
            Row(modifier = Modifier.fillMaxWidth()) {
                Text("Bowler", modifier = Modifier.weight(2.5f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("O", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("M", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("R", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("W", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                Text("ECO", modifier = Modifier.weight(1.1f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))

            inning.bowlerScores.forEach { bow ->
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(bow.player.name, modifier = Modifier.weight(2.5f), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Text(bow.oversString, modifier = Modifier.weight(0.8f), fontSize = 12.sp)
                    Text(bow.maidens.toString(), modifier = Modifier.weight(0.8f), fontSize = 12.sp)
                    Text(bow.runsConceded.toString(), modifier = Modifier.weight(0.8f), fontSize = 12.sp)
                    Text(bow.wickets.toString(), modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, color = CricSecondary, fontSize = 13.sp)
                    Text(String.format("%.2f", bow.economy), modifier = Modifier.weight(1.1f), fontSize = 12.sp, color = CricTextSecondary)
                }
                HorizontalDivider(color = Color(0xFFF8FAFC))
            }
        }
    }
}
