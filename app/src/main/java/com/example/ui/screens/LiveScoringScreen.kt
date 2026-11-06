package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChangeCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Translate
import com.example.data.CommentaryItem
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BallEvent
import com.example.data.BatsmanScore
import com.example.data.DismissalType
import com.example.data.ExtraType
import com.example.data.MatchStatus
import com.example.data.Player
import com.example.ui.theme.CricDarkCard
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricGreen
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTealLight
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel
import com.example.ui.components.CelebrationOverlay
import androidx.compose.material.icons.filled.Refresh

@Composable
fun LiveScoringScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isSpectatorMode by viewModel.isSpectatorMode.collectAsState()
    val match by viewModel.currentMatch.collectAsState()
    val celebrationEvent by viewModel.celebrationEvent.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()
    val isOverCompleteModalOpen by viewModel.isOverCompleteModalOpen.collectAsState()
    val isWicketModalOpen by viewModel.isWicketModalOpen.collectAsState()
    val isBowlerModalOpen by viewModel.isBowlerModalOpen.collectAsState()
    val isMatchFinishedModalOpen by viewModel.isMatchFinishedModalOpen.collectAsState()

    val isOfflineMode by viewModel.isOfflineMode.collectAsState()
    val pendingOfflineEvents by viewModel.pendingOfflineEvents.collectAsState()
    val isSyncing by viewModel.isSyncing.collectAsState()
    val commentaryList by viewModel.commentaryList.collectAsState()
    val commentaryLanguage by viewModel.commentaryLanguage.collectAsState()
    var scoringScreenTab by remember { mutableIntStateOf(0) } // 0: Scoring Pad, 1: Auto-Commentary, 2: Fantasy 11

    val activeMatch = match
    if (activeMatch == null) {
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
                text = "No Live Match",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "To start live scoring, first enter team names and add players in match setup.",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontSize = 13.sp,
                color = CricTextSecondary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { viewModel.navigateTo(AppScreen.MatchSetup) },
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Start a Match", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedButton(onClick = onBack) {
                Text("Back to Home")
            }
        }
        return
    }

    val currentInning = activeMatch.currentInning

    var showExtrasDialog by remember { mutableStateOf(false) }
    var selectedExtraType by remember { mutableStateOf<ExtraType?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .testTag("live_scoring_screen_container")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .testTag("live_scoring_screen")
        ) {
            // Top App Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack, modifier = Modifier.testTag("scoring_back_button")) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Column {
                            Text(
                                text = "${activeMatch.teamA.shortName} vs ${activeMatch.teamB.shortName}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = if (activeMatch.currentInningIndex == 0) "1st Innings" else "2nd Innings",
                                fontSize = 12.sp,
                                color = CricTeal,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Refresh button (Requirement 11)
                        IconButton(
                            onClick = { viewModel.refreshData() },
                            modifier = Modifier.size(34.dp).testTag("live_refresh_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Scores",
                                tint = CricTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        if (isSpectatorMode) {
                            Surface(
                                color = Color(0xFFDC2626),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "🔴 LIVE VIEWER",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        } else {
                            // Undo last ball button (Only enabled in scorer mode)
                            IconButton(
                                onClick = { viewModel.undoLastBall() },
                                modifier = Modifier.testTag("undo_button")
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Undo, contentDescription = "Undo", tint = CricSecondary)
                            }
                        }

                        // Scorecard button
                        OutlinedButton(
                            onClick = { viewModel.navigateTo(AppScreen.FullScorecard) },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(34.dp).testTag("full_scorecard_nav_button")
                        ) {
                            Text("Scorecard", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

        // Offline Ground Mode & Cloud Sync Banner (Requirement 1)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleOfflineMode() }
                .testTag("offline_sync_banner"),
            color = if (isOfflineMode) Color(0xFFFEF3C7) else Color(0xFFECFDF5)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = CircleShape,
                        color = if (isOfflineMode) Color(0xFFD97706) else Color(0xFF10B981),
                        modifier = Modifier.size(8.dp)
                    ) {}
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOfflineMode) "⚡ Offline Ground Mode (${pendingOfflineEvents.size} queued)" else "● Online • Cloud Synced",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = if (isOfflineMode) Color(0xFFB45309) else Color(0xFF047857)
                    )
                }
                if (isOfflineMode) {
                    Button(
                        onClick = { viewModel.syncOfflineData() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.height(26.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                    ) {
                        Text(if (isSyncing) "Syncing..." else "Sync Now", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text("Tap for Offline", fontSize = 10.sp, color = CricTextSecondary)
                }
            }
        }

        // Mode Switcher Banner: Easily toggle between Live Spectator Mode and Match Scorer Mode
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE2E8F0))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isSpectatorMode) Color(0xFFDC2626) else Color.Transparent)
                            .clickable { viewModel.setSpectatorMode(true) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("toggle_spectator_mode_button")
                    ) {
                        Text(
                            text = "🔴 Viewer Mode",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSpectatorMode) Color.White else CricTextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (!isSpectatorMode) CricTeal else Color.Transparent)
                            .clickable { viewModel.setSpectatorMode(false) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                            .testTag("toggle_scorer_mode_button")
                    ) {
                        Text(
                            text = "✏️ Scorer",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (!isSpectatorMode) Color.White else CricTextSecondary
                        )
                    }
                }

                Text(
                    text = if (isSpectatorMode) "Read-only Live Score" else "Scoring Keypad Active",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSpectatorMode) Color(0xFFDC2626) else CricTeal
                )
            }
        }

        // Scrollable content: Scorecard summary + Batsmen + Bowler + Over Strip + Keypad
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 16.dp)
        ) {
            // Main Live Score Header Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .shadow(3.dp, RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = currentInning?.battingTeam?.name ?: activeMatch.teamA.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Toss: ${activeMatch.tossWinner?.name ?: activeMatch.teamA.name} elected to ${activeMatch.tossDecision}",
                                fontSize = 11.sp,
                                color = CricTextSecondary
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.Bottom) {
                                Text(
                                    text = "${currentInning?.totalRuns ?: 0}/${currentInning?.wickets ?: 0}",
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = CricSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${currentInning?.oversString ?: "0.0"}/${activeMatch.totalOvers})",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CricTextSecondary,
                                    modifier = Modifier.padding(bottom = 3.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(6.dp))

                    // CRR & Target Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "CRR: ${String.format("%.2f", currentInning?.runRate ?: 0f)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = CricTeal
                        )

                        if (activeMatch.currentInningIndex == 1 && activeMatch.target != null) {
                            Text(
                                text = "Target: ${activeMatch.target} | Need ${activeMatch.requiredRuns} in ${activeMatch.remainingBalls}b",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = CricSecondary
                            )
                        } else {
                            Text(
                                text = "Extras: ${currentInning?.extras?.total ?: 0} (wd ${currentInning?.extras?.wides ?: 0}, nb ${currentInning?.extras?.noBalls ?: 0}, lb ${currentInning?.extras?.legByes ?: 0})",
                                fontSize = 11.sp,
                                color = CricTextSecondary
                            )
                        }
                    }
                }
            }

            // Current Batsmen on Crease Table
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Header row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Batter", modifier = Modifier.weight(2.5f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                        Text("R", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                        Text("B", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                        Text("4s", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                        Text("6s", modifier = Modifier.weight(0.8f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                        Text("SR", modifier = Modifier.weight(1.2f), fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Striker row
                    val striker = currentInning?.striker
                    if (striker != null) {
                        BatsmanRowItem(score = striker, isStriker = true)
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Non-Striker row
                    val nonStriker = currentInning?.nonStriker
                    if (nonStriker != null) {
                        BatsmanRowItem(score = nonStriker, isStriker = false)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isSpectatorMode) {
                        Spacer(modifier = Modifier.height(8.dp))
                        // Swap Strike quick button
                        OutlinedButton(
                            onClick = { viewModel.swapStrike() },
                            modifier = Modifier.fillMaxWidth().height(32.dp).testTag("swap_strike_button"),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Swap Strike", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Current Bowler Card
            val bowler = currentInning?.activeBowler
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = bowler?.player?.name ?: "Select Bowler",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Text(
                            text = "O: ${bowler?.oversString ?: "0.0"} | M: ${bowler?.maidens ?: 0} | R: ${bowler?.runsConceded ?: 0} | W: ${bowler?.wickets ?: 0} | Eco: ${String.format("%.2f", bowler?.economy ?: 0f)}",
                            fontSize = 11.sp,
                            color = CricTextSecondary
                        )
                    }

                    if (!isSpectatorMode) {
                        OutlinedButton(
                            onClick = { viewModel.isBowlerModalOpen.value = true },
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.height(32.dp).testTag("change_bowler_button")
                        ) {
                            Text("Change", fontSize = 11.sp)
                        }
                    }
                }
            }

            // This Over Ball Strip (Timeline)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("This Over", fontSize = 11.sp, color = CricTextSecondary, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val recentBalls = currentInning?.recentBalls ?: emptyList()
                        if (recentBalls.isEmpty()) {
                            Text("New over started...", fontSize = 12.sp, color = CricTextSecondary)
                        } else {
                            recentBalls.forEach { ball ->
                                BallBadgeItem(ball = ball)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Scoring, Auto-Commentary & Fantasy 11 Tabs (Requirement 2 & 5)
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(10.dp)
            ) {
                TabRow(
                    selectedTabIndex = scoringScreenTab,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    Tab(
                        selected = scoringScreenTab == 0,
                        onClick = { scoringScreenTab = 0 },
                        text = { Text(if (isSpectatorMode) "Live Hub" else "Keypad", fontSize = 12.sp, fontWeight = if (scoringScreenTab == 0) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = scoringScreenTab == 1,
                        onClick = { scoringScreenTab = 1 },
                        text = { Text("Commentary (${commentaryList.size})", fontSize = 12.sp, fontWeight = if (scoringScreenTab == 1) FontWeight.Bold else FontWeight.Normal) }
                    )
                    Tab(
                        selected = scoringScreenTab == 2,
                        onClick = { scoringScreenTab = 2 },
                        text = { Text("Fantasy 11 Pts", fontSize = 12.sp, fontWeight = if (scoringScreenTab == 2) FontWeight.Bold else FontWeight.Normal) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            when (scoringScreenTab) {
                0 -> {
                    // TAB 0: KEYPAD / SCORER OR SPECTATOR VIEW
                    if (!isSpectatorMode) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .shadow(4.dp, RoundedCornerShape(16.dp))
                                .testTag("scoring_keypad_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                // Row 1: Primary Runs (0, 1, 2, 3)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ScoringNumberButton("0", "Dot", Modifier.weight(1f)) { viewModel.recordRuns(0) }
                                    ScoringNumberButton("1", "", Modifier.weight(1f)) { viewModel.recordRuns(1) }
                                    ScoringNumberButton("2", "", Modifier.weight(1f)) { viewModel.recordRuns(2) }
                                    ScoringNumberButton("3", "", Modifier.weight(1f)) { viewModel.recordRuns(3) }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Row 2: Boundary Runs (4, 6) & Quick Extras
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ScoringSpecialButton("4", "FOUR", Color(0xFF2563EB), Modifier.weight(1f)) { viewModel.recordRuns(4) }
                                    ScoringSpecialButton("6", "SIX", CricGreen, Modifier.weight(1f)) { viewModel.recordRuns(6) }
                                    ScoringExtraButton("WD", Modifier.weight(1f)) { viewModel.recordExtra(ExtraType.WIDE, 1) }
                                    ScoringExtraButton("NB", Modifier.weight(1f)) { viewModel.recordExtra(ExtraType.NO_BALL, 1) }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Row 3: BYE, LB, and BIG OUT / WICKET BUTTON
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ScoringExtraButton("BYE", Modifier.weight(1f)) { viewModel.recordExtra(ExtraType.BYE, 1) }
                                    ScoringExtraButton("LB", Modifier.weight(1f)) { viewModel.recordExtra(ExtraType.LEG_BYE, 1) }

                                    // BIG RED OUT BUTTON
                                    Button(
                                        onClick = { viewModel.isWicketModalOpen.value = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = CricSecondary),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier
                                            .weight(2f)
                                            .height(48.dp)
                                            .testTag("wicket_out_button")
                                    ) {
                                        Text("OUT / WICKET", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                                    }
                                }
                            }
                        }
                    } else {
                        // SPECTATOR MODE
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp)
                                .shadow(3.dp, RoundedCornerShape(16.dp))
                                .testTag("spectator_view_hub_card"),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(Color(0xFFDC2626))
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Live Spectator Mode",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                    Surface(
                                        color = Color(0xFFDCFCE7),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "⚡ LIVE UPDATE",
                                            fontSize = 10.sp,
                                            color = Color(0xFF16A34A),
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "🔒 Scoring access is restricted to the match scorer. Real-time updates appear automatically.",
                                    fontSize = 12.sp,
                                    color = CricTextSecondary,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFFF1F5F9))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("Current Partnership", fontSize = 11.sp, color = CricTextSecondary)
                                        val pRuns = (currentInning?.striker?.runs ?: 0) + (currentInning?.nonStriker?.runs ?: 0)
                                        val pBalls = (currentInning?.striker?.balls ?: 0) + (currentInning?.nonStriker?.balls ?: 0)
                                        Text(
                                            text = "$pRuns runs ($pBalls balls)",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Projected Score (${activeMatch.totalOvers} Ov)", fontSize = 11.sp, color = CricTextSecondary)
                                        val projected = ((currentInning?.runRate ?: 0f) * activeMatch.totalOvers).toInt()
                                        Text(
                                            text = "$projected runs",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            color = CricTeal
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                OutlinedButton(
                                    onClick = { viewModel.setSpectatorMode(false) },
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("spectator_switch_to_scorer_btn")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Switch to Scorer Mode", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // TAB 1: AUTO-TEXT COMMENTARY (GUJARATI / HINDI / ENGLISH)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Translate, contentDescription = null, tint = CricTeal, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Auto-Text Commentary", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf("Gujarati", "Hindi", "English").forEach { lang ->
                                        val isSel = commentaryLanguage.equals(lang, ignoreCase = true)
                                        Surface(
                                            shape = RoundedCornerShape(12.dp),
                                            color = if (isSel) CricTeal else Color(0xFFF1F5F9),
                                            modifier = Modifier.clickable { viewModel.setCommentaryLanguage(lang) }
                                        ) {
                                            Text(
                                                text = when (lang) {
                                                    "Gujarati" -> "ગુજરાતી"
                                                    "Hindi" -> "हिंदी"
                                                    else -> "EN"
                                                },
                                                fontSize = 10.sp,
                                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                                color = if (isSel) Color.White else Color(0xFF475569),
                                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Column(
                                modifier = Modifier
                                    .heightIn(max = 240.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (commentaryList.isEmpty()) {
                                    Text("No balls bowled yet in this match.", fontSize = 12.sp, color = CricTextSecondary)
                                } else {
                                    commentaryList.forEach { comm ->
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = when {
                                                comm.isSix -> Color(0xFFECFDF5)
                                                comm.isBoundary -> Color(0xFFEFF6FF)
                                                comm.isWicket -> Color(0xFFFEF2F2)
                                                else -> Color(0xFFF8FAFC)
                                            },
                                            border = BorderStroke(
                                                0.5.dp,
                                                when {
                                                    comm.isSix -> Color(0xFFA7F3D0)
                                                    comm.isBoundary -> Color(0xFFBFDBFE)
                                                    comm.isWicket -> Color(0xFFFECACA)
                                                    else -> Color(0xFFE2E8F0)
                                                }
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.padding(8.dp)) {
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.SpaceBetween,
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    Text(
                                                        text = "Ov ${comm.overText} • ${comm.headline}",
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 11.sp,
                                                        color = when {
                                                            comm.isSix -> Color(0xFF047857)
                                                            comm.isBoundary -> Color(0xFF1D4ED8)
                                                            comm.isWicket -> Color(0xFFB91C1C)
                                                            else -> MaterialTheme.colorScheme.onSurface
                                                        }
                                                    )
                                                    Text(comm.timestamp, fontSize = 9.sp, color = CricTextSecondary)
                                                }
                                                Spacer(modifier = Modifier.height(2.dp))
                                                val commentText = when (commentaryLanguage.lowercase()) {
                                                    "gujarati" -> comm.commentaryGujarati
                                                    "hindi" -> comm.commentaryHindi
                                                    else -> comm.commentaryEnglish
                                                }
                                                Text(
                                                    text = commentText,
                                                    fontSize = 12.sp,
                                                    lineHeight = 16.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: LIVE FANTASY 11 POINTS
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Match Fantasy 11 Points", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("Real-time points (100% Free Fan League)", fontSize = 10.sp, color = CricTextSecondary)
                                }
                                Button(
                                    onClick = { viewModel.navigateTo(AppScreen.LocalFantasy) },
                                    colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("Create My 11", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Column(
                                modifier = Modifier
                                    .heightIn(max = 240.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                val allPlayers = (activeMatch.teamA.players + activeMatch.teamB.players).distinctBy { it.id }
                                allPlayers.forEach { p ->
                                    val bScore = currentInning?.batsmenScores?.find { it.player.id == p.id }
                                    val bwScore = currentInning?.bowlerScores?.find { it.player.id == p.id }
                                    val runsPts = (bScore?.runs ?: 0) + (bScore?.fours ?: 0) * 1 + (bScore?.sixes ?: 0) * 2 + (if ((bScore?.runs ?: 0) >= 50) 8 else 0)
                                    val wktPts = (bwScore?.wickets ?: 0) * 25 + (if ((bwScore?.wickets ?: 0) >= 3) 8 else 0) + (bwScore?.maidens ?: 0) * 8
                                    val totalPts = runsPts + wktPts + (p.catches * 8)

                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color(0xFFF8FAFC),
                                        border = BorderStroke(0.5.dp, Color(0xFFE2E8F0)),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column {
                                                Text(p.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text("${p.role} • ${bScore?.runs ?: 0}r, ${bwScore?.wickets ?: 0}w", fontSize = 10.sp, color = CricTextSecondary)
                                            }
                                            Text("$totalPts pts", fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = CricTeal)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // WICKET DIALOG
    if (isWicketModalOpen) {
        WicketDismissalDialog(
            inning = currentInning,
            bowlingTeam = currentInning?.bowlingTeam ?: activeMatch.teamB,
            battingTeam = currentInning?.battingTeam ?: activeMatch.teamA,
            onDismiss = { viewModel.isWicketModalOpen.value = false },
            onConfirmWicket = { dismissalType, dismissedId, newBatsman, fielder ->
                viewModel.recordWicket(dismissalType, dismissedId, newBatsman, fielder)
            }
        )
    }

    // OVER COMPLETED DIALOG
    if (isOverCompleteModalOpen) {
        OverCompleteDialog(
            currentBowlerName = currentInning?.activeBowler?.player?.name ?: "",
            bowlingTeam = currentInning?.bowlingTeam ?: activeMatch.teamB,
            onSelectNextBowler = { nextBowler ->
                viewModel.changeBowler(nextBowler)
            }
        )
    }

    // SELECT BOWLER DIALOG (Manual change)
    if (isBowlerModalOpen) {
        SelectBowlerDialog(
            bowlingTeam = currentInning?.bowlingTeam ?: activeMatch.teamB,
            currentBowlerId = currentInning?.currentBowlerId ?: "",
            onSelectBowler = { viewModel.changeBowler(it) },
            onDismiss = { viewModel.isBowlerModalOpen.value = false }
        )
    }

    // MATCH FINISHED / INNING COMPLETE DIALOG
    if (isMatchFinishedModalOpen) {
        MatchFinishedDialog(
            match = activeMatch,
            onClose = {
                viewModel.isMatchFinishedModalOpen.value = false
                viewModel.navigateTo(AppScreen.FullScorecard)
            },
            onStartSecondInnings = { striker, nonStriker, openingBowler ->
                viewModel.isMatchFinishedModalOpen.value = false
                viewModel.startSecondInnings(striker, nonStriker, openingBowler)
            }
        )
    }

    // Firecrackers Celebration Overlay on 4, 6, Wicket (Requirement 10)
    CelebrationOverlay(
        celebration = celebrationEvent,
        onDismiss = { viewModel.clearCelebration() }
    )
    }
}

@Composable
fun BatsmanRowItem(score: BatsmanScore, isStriker: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isStriker) Color(0xFFFEF3C7).copy(alpha = 0.5f) else Color.Transparent)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = (if (isStriker) "* " else "  ") + score.player.name,
            modifier = Modifier.weight(2.5f),
            fontWeight = if (isStriker) FontWeight.Bold else FontWeight.Medium,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(score.runs.toString(), modifier = Modifier.weight(0.8f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text(score.balls.toString(), modifier = Modifier.weight(0.8f), fontSize = 12.sp, color = CricTextSecondary)
        Text(score.fours.toString(), modifier = Modifier.weight(0.8f), fontSize = 12.sp, color = CricTextSecondary)
        Text(score.sixes.toString(), modifier = Modifier.weight(0.8f), fontSize = 12.sp, color = CricTextSecondary)
        Text(String.format("%.1f", score.strikeRate), modifier = Modifier.weight(1.2f), fontSize = 12.sp, color = CricTextSecondary)
    }
}

@Composable
fun BallBadgeItem(ball: BallEvent) {
    val bgColor = when {
        ball.isWicket -> CricSecondary
        ball.runsScored == 6 -> CricGreen
        ball.runsScored == 4 -> Color(0xFF2563EB)
        ball.extraType != null -> Color(0xFFD97706)
        ball.runsScored == 0 -> Color(0xFFE2E8F0)
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val textColor = when {
        ball.isWicket || ball.runsScored == 6 || ball.runsScored == 4 || ball.extraType != null -> Color.White
        else -> MaterialTheme.colorScheme.onSurface
    }

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = ball.displayText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = textColor
        )
    }
}

@Composable
fun ScoringNumberButton(label: String, sublabel: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.height(48.dp).testTag("score_btn_$label")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            if (sublabel.isNotEmpty()) {
                Text(sublabel, fontSize = 9.sp, color = CricTextSecondary)
            }
        }
    }
}

@Composable
fun ScoringSpecialButton(label: String, sublabel: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = color),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.height(48.dp).testTag("score_btn_$label")
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = Color.White)
            Text(sublabel, fontSize = 8.sp, color = Color.White.copy(alpha = 0.9f))
        }
    }
}

@Composable
fun ScoringExtraButton(label: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.height(48.dp).testTag("score_btn_$label")
    ) {
        Text(label, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}

// WICKET SELECTION DIALOG
@Composable
fun WicketDismissalDialog(
    inning: com.example.data.Inning?,
    bowlingTeam: com.example.data.Team,
    battingTeam: com.example.data.Team,
    onDismiss: () -> Unit,
    onConfirmWicket: (DismissalType, String, Player, String?) -> Unit
) {
    var selectedType by remember { mutableStateOf(DismissalType.BOWLED) }
    var selectedDismissedId by remember { mutableStateOf(inning?.currentStrikerId ?: "") }
    var fielderName by remember { mutableStateOf("") }

    val remainingPlayers = battingTeam.players.filter { player ->
        inning?.batsmenScores?.none { it.player.id == player.id } ?: true
    }.ifEmpty {
        listOf(
            Player("sub_batter_${System.currentTimeMillis()}", "Next Batsman", "Naliya", role = "Batsman")
        )
    }

    var selectedNewBatsman by remember { mutableStateOf(remainingPlayers.first()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("How was the Wicket?", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text("Dismissal Type:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    DismissalType.values().forEach { type ->
                        val isSel = (type == selectedType)
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { selectedType = type },
                            color = if (isSel) CricSecondary else Color(0xFFF1F5F9)
                        ) {
                            Text(
                                text = type.label,
                                color = if (isSel) Color.White else CricTextPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Batsman Dismissed
                Text("Dismissed Batter:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.padding(top = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val striker = inning?.striker
                    val nonStriker = inning?.nonStriker
                    if (striker != null) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedDismissedId = striker.player.id },
                            color = if (selectedDismissedId == striker.player.id) CricTealLight else Color(0xFFF1F5F9)
                        ) {
                            Text("* ${striker.player.name}", fontSize = 12.sp, modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold)
                        }
                    }
                    if (nonStriker != null) {
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { selectedDismissedId = nonStriker.player.id },
                            color = if (selectedDismissedId == nonStriker.player.id) CricTealLight else Color(0xFFF1F5F9)
                        ) {
                            Text(nonStriker.player.name, fontSize = 12.sp, modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                if (selectedType == DismissalType.CAUGHT || selectedType == DismissalType.RUN_OUT || selectedType == DismissalType.STUMPED) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (selectedType == DismissalType.CAUGHT) "Caught by:" else "Select Fielder:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricTeal
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Fielders list chips from bowling team (Requirement 4)
                    val fieldingPlayers = bowlingTeam.players.ifEmpty {
                        listOf(
                            Player("f1", "Wicketkeeper", "Naliya", role = "Wk-Batsman"),
                            Player("f2", "Slip Fielder", "Naliya", role = "Batsman"),
                            Player("f3", "Point Fielder", "Naliya", role = "All-rounder"),
                            Player("f4", "Cover Fielder", "Naliya", role = "Bowler")
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        fieldingPlayers.forEach { fielder ->
                            val isFielderSelected = fielderName == fielder.name
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { fielderName = fielder.name },
                                color = if (isFielderSelected) CricTeal else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isFielderSelected) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                    }
                                    Text(
                                        text = fielder.name,
                                        fontSize = 12.sp,
                                        fontWeight = if (isFielderSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isFielderSelected) Color.White else CricTextPrimary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fielderName,
                        onValueChange = { fielderName = it },
                        label = { Text("Or enter custom fielder name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Select Next Batsman
                Text("Next Batter:", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Column(modifier = Modifier.padding(top = 4.dp)) {
                    remainingPlayers.take(4).forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedNewBatsman = p }
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .border(2.dp, if (selectedNewBatsman.id == p.id) CricTeal else Color.Gray, CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(p.name, fontSize = 13.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirmWicket(selectedType, selectedDismissedId, selectedNewBatsman, fielderName.ifBlank { null })
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricSecondary)
            ) {
                Text("Confirm Out")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// OVER COMPLETED DIALOG
@Composable
fun OverCompleteDialog(
    currentBowlerName: String,
    bowlingTeam: com.example.data.Team,
    onSelectNextBowler: (Player) -> Unit
) {
    val availableBowlers = bowlingTeam.players.filterNot { it.name == currentBowlerName }.ifEmpty {
        bowlingTeam.players
    }

    var selectedBowler by remember { mutableStateOf(availableBowlers.firstOrNull() ?: bowlingTeam.players.first()) }

    AlertDialog(
        onDismissRequest = {},
        title = { Text("Over Completed!", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text("Choose the next bowler for this over:", fontSize = 13.sp)
                Spacer(modifier = Modifier.height(10.dp))
                availableBowlers.forEach { b ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedBowler = b }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .border(2.dp, if (selectedBowler.id == b.id) CricTeal else Color.Gray, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(b.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(b.bowlingStyle, fontSize = 11.sp, color = CricTextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSelectNextBowler(selectedBowler) },
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal)
            ) {
                Text("Start Next Over")
            }
        }
    )
}

// SELECT BOWLER DIALOG
@Composable
fun SelectBowlerDialog(
    bowlingTeam: com.example.data.Team,
    currentBowlerId: String,
    onSelectBowler: (Player) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Bowler", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                bowlingTeam.players.forEach { b ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectBowler(b) }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.SportsCricket, contentDescription = null, tint = CricTeal)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(b.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(b.bowlingStyle, fontSize = 11.sp, color = CricTextSecondary)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            OutlinedButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

// MATCH FINISHED / INNING COMPLETE CELEBRATION
@Composable
fun MatchFinishedDialog(
    match: com.example.data.CricketMatch,
    onClose: () -> Unit,
    onStartSecondInnings: (Player, Player, Player) -> Unit
) {
    val isFirstInningEnd = (match.currentInningIndex == 0)
    val firstInning = match.innings.getOrNull(0)
    val secondInningBattingTeam = firstInning?.bowlingTeam ?: match.teamB
    val secondInningBowlingTeam = firstInning?.battingTeam ?: match.teamA

    val availableBatsmen = secondInningBattingTeam.players.ifEmpty {
        listOf(
            Player("b1", "Opening Batter 1", "Naliya"),
            Player("b2", "Opening Batter 2", "Naliya")
        )
    }

    val availableBowlers = secondInningBowlingTeam.players.ifEmpty {
        listOf(
            Player("bw1", "Opening Bowler", "Bhuj")
        )
    }

    var striker by remember { mutableStateOf(availableBatsmen[0]) }
    var nonStriker by remember { mutableStateOf(availableBatsmen.getOrElse(1) { availableBatsmen[0] }) }
    var openingBowler by remember { mutableStateOf(availableBowlers[0]) }

    AlertDialog(
        onDismissRequest = onClose,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = CricGold, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isFirstInningEnd) "Innings Complete!" else "Match Finished!",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column {
                if (isFirstInningEnd) {
                    Text(
                        text = "${firstInning?.battingTeam?.name} scored ${firstInning?.totalRuns}/${firstInning?.wickets} in ${firstInning?.oversString} overs.",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Target for ${secondInningBattingTeam.name}: ${(firstInning?.totalRuns ?: 0) + 1} runs",
                        fontWeight = FontWeight.Bold,
                        color = CricTeal,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Select 2nd Innings Openers:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Striker: ${striker.name}", fontSize = 12.sp)
                    Text("Non-Striker: ${nonStriker.name}", fontSize = 12.sp)
                    Text("Bowler: ${openingBowler.name}", fontSize = 12.sp)
                } else {
                    Text(
                        text = match.resultMessage ?: "Match concluded successfully!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricGreen
                    )
                }
            }
        },
        confirmButton = {
            if (isFirstInningEnd) {
                Button(
                    onClick = { onStartSecondInnings(striker, nonStriker, openingBowler) },
                    colors = ButtonDefaults.buttonColors(containerColor = CricTeal)
                ) {
                    Text("Start 2nd Innings")
                }
            } else {
                Button(onClick = onClose, colors = ButtonDefaults.buttonColors(containerColor = CricTeal)) {
                    Text("View Scorecard")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onClose) { Text("Close") }
        }
    )
}
