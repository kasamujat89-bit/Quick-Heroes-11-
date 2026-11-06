package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Player
import com.example.data.SampleData
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

@Composable
fun LocalFantasyScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val fantasyTeam by viewModel.userFantasyTeam.collectAsState()
    val leaderboard by viewModel.fantasyLeaderboard.collectAsState()
    val currentMatch by viewModel.currentMatch.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Select XI, 1: Leaderboard, 2: Rules
    var teamNameInput by remember { mutableStateOf(fantasyTeam.teamName) }

    // Combine players from current match or sample roster
    val availablePlayers: List<Player> = if (currentMatch != null) {
        val playersA = currentMatch!!.teamA.players
        val playersB = currentMatch!!.teamB.players
        (playersA + playersB).distinctBy { it.id }
    } else {
        SampleData.players
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("local_fantasy_screen")
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("fantasy_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Local Fantasy League",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "100% Free Grassroots Fan Fantasy • Virtual Points",
                        fontSize = 11.sp,
                        color = Color(0xFF059669),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("My Virtual XI (${fantasyTeam.selectedPlayerIds.size}/11)", fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("Fan Leaderboard", fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("Point Rules", fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        when (selectedTab) {
            0 -> {
                // Create / Edit Fantasy XI Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = Color(0xFF059669), modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "100% Free Grassroots Fantasy",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = Color(0xFF065F46)
                                    )
                                    Text(
                                        text = "Pick 11 local players for this match. Choose 1 Captain (2x pts) and 1 Vice-Captain (1.5x pts). Zero money, 100% pure cricket fun!",
                                        fontSize = 11.sp,
                                        color = Color(0xFF047857),
                                        lineHeight = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = teamNameInput,
                            onValueChange = { teamNameInput = it },
                            label = { Text("Your Fantasy Team Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Selected: ${fantasyTeam.selectedPlayerIds.size}/11 Players",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Button(
                                onClick = { viewModel.saveFantasyTeam(teamNameInput) },
                                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                                enabled = fantasyTeam.selectedPlayerIds.size == 11,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save Team", fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    items(availablePlayers, key = { it.id }) { p ->
                        val isSelected = fantasyTeam.selectedPlayerIds.contains(p.id)
                        val isCaptain = fantasyTeam.captainId == p.id
                        val isViceCaptain = fantasyTeam.viceCaptainId == p.id

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) CricTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) BorderStroke(1.5.dp, CricTeal) else null
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = CircleShape,
                                            color = if (isSelected) CricTeal else Color(0xFFCBD5E1),
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                if (isSelected) {
                                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                                } else {
                                                    Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                                }
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(p.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                            Text("${p.role} • ${p.city} • ${p.runs} runs, ${p.wickets} wkts", fontSize = 11.sp, color = CricTextSecondary)
                                        }
                                    }

                                    OutlinedButton(
                                        onClick = { viewModel.toggleFantasyPlayer(p.id) },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = if (isSelected) ButtonDefaults.outlinedButtonColors(containerColor = CricTeal.copy(alpha = 0.15f)) else ButtonDefaults.outlinedButtonColors()
                                    ) {
                                        Text(if (isSelected) "Remove" else "Add XI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                if (isSelected) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.End,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isCaptain) CricGold else Color(0xFFE2E8F0),
                                            modifier = Modifier.clickable { viewModel.setFantasyCaptain(p.id) }
                                        ) {
                                            Text(
                                                text = if (isCaptain) "★ CAPTAIN (2x)" else "Make C (2x)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isCaptain) Color.Black else Color(0xFF475569),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = if (isViceCaptain) Color(0xFF93C5FD) else Color(0xFFE2E8F0),
                                            modifier = Modifier.clickable { viewModel.setFantasyViceCaptain(p.id) }
                                        ) {
                                            Text(
                                                text = if (isViceCaptain) "★ VC (1.5x)" else "Make VC (1.5x)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isViceCaptain) Color(0xFF1E3A8A) else Color(0xFF475569),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            1 -> {
                // Leaderboard Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Match Fantasy Standings", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text("Virtual points earned by spectators and fans in real time", fontSize = 12.sp, color = CricTextSecondary)
                            }
                        }
                    }

                    items(leaderboard, key = { it.rank }) { entry ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (entry.isCurrentUser) Color(0xFFECFDF5) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (entry.isCurrentUser) BorderStroke(1.dp, Color(0xFFA7F3D0)) else null
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        shape = CircleShape,
                                        color = when (entry.rank) {
                                            1 -> CricGold
                                            2 -> Color(0xFF94A3B8)
                                            3 -> Color(0xFFD97706)
                                            else -> Color(0xFFE2E8F0)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = "#${entry.rank}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = if (entry.rank <= 3) Color.White else Color(0xFF334155)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(entry.userName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text("${entry.teamName} • ${entry.captainName}", fontSize = 11.sp, color = CricTextSecondary)
                                    }
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("${entry.points} pts", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = CricTeal)
                                    if (entry.isCurrentUser) {
                                        Text("Your Team", fontSize = 10.sp, color = Color(0xFF059669), fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            2 -> {
                // Fantasy Point System Rules
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text("Grassroots Fantasy Point System", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Points update live ball-by-ball during scoring:", fontSize = 12.sp, color = CricTextSecondary)
                    }

                    val rules = listOf(
                        "Batting: 1 Run" to "+1 Point",
                        "Boundary Bonus (4s)" to "+1 Extra Point",
                        "Six Bonus (6s)" to "+2 Extra Points",
                        "Half-Century (50 Runs)" to "+8 Bonus Points",
                        "Century (100 Runs)" to "+16 Bonus Points",
                        "Duck Dismissal" to "-2 Points",
                        "Bowling: Wicket" to "+25 Points",
                        "3-Wicket Haul Bonus" to "+8 Bonus Points",
                        "5-Wicket Haul Bonus (Fifer)" to "+16 Bonus Points",
                        "Maiden Over" to "+8 Points",
                        "Fielding: Catch" to "+8 Points",
                        "Fielding: Stumping" to "+12 Points",
                        "Fielding: Run Out" to "+8 Points",
                        "Captain Multiplier" to "2x Total Points",
                        "Vice-Captain Multiplier" to "1.5x Total Points"
                    )

                    items(rules) { (label, value) ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(label, fontSize = 13.sp)
                                Text(value, fontWeight = FontWeight.Bold, color = CricTeal, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
