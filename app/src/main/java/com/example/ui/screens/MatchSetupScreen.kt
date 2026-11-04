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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material.icons.filled.Phone
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Player
import com.example.data.SampleData
import com.example.data.Team
import com.example.ui.theme.CricCardLight
import com.example.ui.theme.CricGreen
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTealLight
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

@Composable
fun MatchSetupScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 3-Step Wizard:
    // Step 0: Teams & Match Settings (ટીમ અને ઓવર્સ)
    // Step 1: Players & Squad (ખેલાડીઓના નામ ઉમેરો)
    // Step 2: Toss & Openers (ટોસ અને ઓપનિંગ ખેલાડીઓ)
    var currentStep by remember { mutableIntStateOf(0) }

    // Step 0: Team Names & Settings
    var teamAName by remember { mutableStateOf("Naliya Royals") }
    var teamBName by remember { mutableStateOf("Kutch Warriors") }
    var venue by remember { mutableStateOf("Naliya Cricket Ground") }

    val matchTypes = listOf("Limited Overs", "Box Cricket", "Tennis Ball")
    var selectedMatchType by remember { mutableStateOf("Limited Overs") }
    var selectedBallType by remember { mutableStateOf("Tennis Ball") }

    val overOptions = listOf(5, 8, 10, 12, 15, 20)
    var selectedOvers by remember { mutableIntStateOf(10) }

    // Step 1: Squad Players list for Team A & Team B (using immutable list in State for reliable recomposition)
    var teamAPlayers by remember {
        mutableStateOf(
            listOf(
                Player("p_a1", "Abuahemad Jat", "Naliya", phone = "9825101122", role = "Batsman"),
                Player("p_a2", "Hasan.a.Jat", "Naliya", phone = "9723412345", role = "All-rounder"),
                Player("p_a3", "Latif Jat (L-J)", "Naliya", phone = "9879054321", role = "Bowler"),
                Player("p_a4", "M s jat", "Naliya", phone = "9909988776", role = "Wk-Batsman"),
                Player("p_a5", "Kasam Jat", "Naliya", phone = "8980294520", role = "All-rounder")
            )
        )
    }

    var teamBPlayers by remember {
        mutableStateOf(
            listOf(
                Player("p_b1", "Imran Pathan", "Bhuj", phone = "9825012345", role = "All-rounder"),
                Player("p_b2", "Hardik Solanki", "Rajkot", phone = "9898123456", role = "Bowler"),
                Player("p_b3", "Suresh Jadeja", "Gandhidham", phone = "9725512345", role = "Batsman"),
                Player("p_b4", "Hussain A", "Naliya", phone = "9426012345", role = "Batsman"),
                Player("p_b5", "Tariq Baloch", "Bhuj", phone = "9879912345", role = "Bowler")
            )
        )
    }

    var activeSquadTab by remember { mutableIntStateOf(0) } // 0 for Team A, 1 for Team B
    var newPlayerNameInput by remember { mutableStateOf("") }
    var newPlayerMobileInput by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("All-rounder") }
    var inputErrorText by remember { mutableStateOf<String?>(null) }
    var successAddMessage by remember { mutableStateOf<String?>(null) }
    var isAddPlayerDialogOpen by remember { mutableStateOf(false) }

    // Dialog state for full custom player
    var dialogPlayerName by remember { mutableStateOf("") }
    var dialogPlayerPhone by remember { mutableStateOf("") }
    var dialogPlayerRole by remember { mutableStateOf("All-rounder") }
    var dialogPlayerCity by remember { mutableStateOf("") }

    val activeList = if (activeSquadTab == 0) teamAPlayers else teamBPlayers
    val activeTeamName = if (activeSquadTab == 0) teamAName.ifBlank { "Team 1" } else teamBName.ifBlank { "Team 2" }

    fun addPlayer(name: String, role: String, city: String = venue, phone: String = newPlayerMobileInput) {
        val trimmed = name.trim()
        if (trimmed.isBlank()) {
            inputErrorText = "કૃપા કરીને ખેલાડીનું નામ લખો (Please enter player name)"
            return
        }
        val newPlayer = Player(
            id = "p_${System.currentTimeMillis()}_${(100..999).random()}",
            name = trimmed,
            city = city.ifBlank { venue.ifBlank { "Kutch" } },
            phone = phone.trim(),
            role = role
        )
        if (activeSquadTab == 0) {
            teamAPlayers = teamAPlayers + newPlayer
        } else {
            teamBPlayers = teamBPlayers + newPlayer
        }
        newPlayerNameInput = ""
        newPlayerMobileInput = ""
        inputErrorText = null
        successAddMessage = "✓ $trimmed ને $activeTeamName માં ઉમેર્યા!"
    }

    // Step 2: Toss & Openers
    var tossWinnerIndex by remember { mutableIntStateOf(0) } // 0 for Team A, 1 for Team B
    var tossDecision by remember { mutableStateOf("Bat") } // "Bat" or "Bowl"

    val battingTeamIsA = (tossWinnerIndex == 0 && tossDecision == "Bat") || (tossWinnerIndex == 1 && tossDecision == "Bowl")
    val battingPlayers = if (battingTeamIsA) teamAPlayers else teamBPlayers
    val bowlingPlayers = if (battingTeamIsA) teamBPlayers else teamAPlayers

    var selectedStriker by remember {
        mutableStateOf(Player("def_s", "Striker", "Naliya"))
    }
    var selectedNonStriker by remember {
        mutableStateOf(Player("def_ns", "Non-Striker", "Naliya"))
    }
    var selectedOpeningBowler by remember {
        mutableStateOf(Player("def_bow", "Bowler", "Bhuj"))
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("match_setup_screen")
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
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (currentStep > 0) currentStep-- else onBack()
                    },
                    modifier = Modifier.testTag("match_setup_back_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Start Match (મેચ લાઇવ કરો)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = when (currentStep) {
                            0 -> "Step 1/3: Team Names & Overs"
                            1 -> "Step 2/3: Player Names & Squads"
                            else -> "Step 3/3: Toss & Playing XI"
                        },
                        fontSize = 12.sp,
                        color = CricTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Stepper Progress Indicator
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StepIndicatorTab(
                stepIndex = 0,
                title = "1. Teams",
                isActive = currentStep == 0,
                isCompleted = currentStep > 0,
                onClick = { currentStep = 0 },
                modifier = Modifier.weight(1f)
            )
            StepIndicatorTab(
                stepIndex = 1,
                title = "2. Players",
                isActive = currentStep == 1,
                isCompleted = currentStep > 1,
                onClick = { currentStep = 1 },
                modifier = Modifier.weight(1f)
            )
            StepIndicatorTab(
                stepIndex = 2,
                title = "3. Toss & Live",
                isActive = currentStep == 2,
                isCompleted = false,
                onClick = { currentStep = 2 },
                modifier = Modifier.weight(1f)
            )
        }

        // Step Content Body
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (currentStep) {
                // ================= STEP 0: TEAMS & MATCH SETTINGS =================
                0 -> {
                    Text("ટીમના નામ (Team Names)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CricTeal)
                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = teamAName,
                        onValueChange = { teamAName = it },
                        label = { Text("Team 1 Name (દા.ત. Naliya Royals)") },
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = CricSecondary) },
                        modifier = Modifier.fillMaxWidth().testTag("team_a_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = teamBName,
                        onValueChange = { teamBName = it },
                        label = { Text("Team 2 Name (દા.ત. Kutch Warriors)") },
                        leadingIcon = { Icon(Icons.Default.Groups, contentDescription = null, tint = CricTeal) },
                        modifier = Modifier.fillMaxWidth().testTag("team_b_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Ground / Venue (ગ્રાઉન્ડ)") },
                        leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = CricTextSecondary) },
                        modifier = Modifier.fillMaxWidth().testTag("venue_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    HorizontalDivider(color = Color(0xFFF1F5F9))
                    Spacer(modifier = Modifier.height(16.dp))

                    Text("મેચ ટાઇપ અને ઓવર્સ (Match Type & Overs)", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = CricTeal)
                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Ball Type:", fontSize = 12.sp, color = CricTextSecondary)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Tennis Ball", "Leather Ball").forEach { ball ->
                            val isSel = (ball == selectedBallType)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedBallType = ball },
                                color = if (isSel) CricTealLight else Color(0xFFF1F5F9),
                                border = if (isSel) CardDefaults.outlinedCardBorder().copy(width = 1.dp) else null
                            ) {
                                Text(
                                    text = ball,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) CricTeal else CricTextPrimary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 12.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Total Overs (ઓવર્સ):", fontSize = 12.sp, color = CricTextSecondary)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        overOptions.forEach { ov ->
                            val isSel = (ov == selectedOvers)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedOvers = ov }
                                    .testTag("overs_option_$ov"),
                                color = if (isSel) CricSecondary else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = "$ov Overs",
                                    color = if (isSel) Color.White else CricTextPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    Button(
                        onClick = { currentStep = 1 },
                        colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("next_to_players_button")
                    ) {
                        Text("Next: Add Players (ખેલાડીઓ ઉમેરો)", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                    }
                }

                // ================= STEP 1: SQUADS & PLAYER NAMES =================
                1 -> {
                    Text("ખેલાડીઓના નામ ઉમેરો (Add Players)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CricTeal)
                    Text("બંને ટીમના ખેલાડીઓના નામ દાખલ કરો જેથી સ્કોરિંગમાં સાચા નામ દેખાય.", fontSize = 12.sp, color = CricTextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Team A vs Team B Tab Selector
                    TabRow(
                        selectedTabIndex = activeSquadTab,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = CricTeal
                    ) {
                        Tab(
                            selected = activeSquadTab == 0,
                            onClick = {
                                activeSquadTab = 0
                                inputErrorText = null
                                successAddMessage = null
                            },
                            text = {
                                Text(
                                    text = "${teamAName.ifBlank { "Team 1" }} (${teamAPlayers.size})",
                                    fontWeight = if (activeSquadTab == 0) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                        Tab(
                            selected = activeSquadTab == 1,
                            onClick = {
                                activeSquadTab = 1
                                inputErrorText = null
                                successAddMessage = null
                            },
                            text = {
                                Text(
                                    text = "${teamBName.ifBlank { "Team 2" }} (${teamBPlayers.size})",
                                    fontWeight = if (activeSquadTab == 1) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 13.sp
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Success Feedback Banner
                    AnimatedVisibility(visible = successAddMessage != null) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            color = Color(0xFFDCFCE7),
                            shape = RoundedCornerShape(10.dp),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF22C55E)))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Color(0xFF15803D),
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = successAddMessage ?: "",
                                        color = Color(0xFF166534),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                }
                                IconButton(
                                    onClick = { successAddMessage = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = Color(0xFF166534),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // ADD PLAYER CARD
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.PersonAdd,
                                    contentDescription = null,
                                    tint = CricTeal,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add to $activeTeamName (ખેલાડી ઉમેરો)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // Text Input Field
                            OutlinedTextField(
                                value = newPlayerNameInput,
                                onValueChange = {
                                    newPlayerNameInput = it
                                    inputErrorText = null
                                },
                                label = { Text("Player Name (ખેલાડીનું નામ)") },
                                placeholder = {
                                    Text(
                                        "દા.ત. Kasam Jat, Latif...",
                                        fontSize = 13.sp,
                                        color = CricTextSecondary
                                    )
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Person, contentDescription = null, tint = CricTeal)
                                },
                                trailingIcon = {
                                    if (newPlayerNameInput.isNotEmpty()) {
                                        IconButton(onClick = { newPlayerNameInput = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                isError = inputErrorText != null,
                                supportingText = if (inputErrorText != null) {
                                    { Text(inputErrorText!!, color = MaterialTheme.colorScheme.error, fontSize = 12.sp) }
                                } else null,
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        addPlayer(newPlayerNameInput, selectedRole)
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_player_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Mobile Number Input Field (Requirement 2)
                            OutlinedTextField(
                                value = newPlayerMobileInput,
                                onValueChange = { newPlayerMobileInput = it },
                                label = { Text("Mobile Number (મોબાઈલ નંબર)") },
                                placeholder = {
                                    Text("દા.ત. 98250 12345", fontSize = 13.sp, color = CricTextSecondary)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = CricTeal)
                                },
                                trailingIcon = {
                                    if (newPlayerMobileInput.isNotEmpty()) {
                                        IconButton(onClick = { newPlayerMobileInput = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                        }
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        addPlayer(newPlayerNameInput, selectedRole, venue, newPlayerMobileInput)
                                    }
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_player_phone_input"),
                                shape = RoundedCornerShape(12.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Role Selection Chips
                            Text("Player Role (રોલ પસંદ કરો):", fontSize = 12.sp, color = CricTextSecondary, fontWeight = FontWeight.Medium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(
                                    "All-rounder" to "⭐ All-rounder",
                                    "Batsman" to "🏏 Batsman",
                                    "Bowler" to "⚡ Bowler",
                                    "Wk-Batsman" to "🧤 Wk-Batsman"
                                ).forEach { (roleKey, roleLabel) ->
                                    val isSelected = selectedRole == roleKey
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .clickable { selectedRole = roleKey },
                                        color = if (isSelected) CricTeal else MaterialTheme.colorScheme.surfaceVariant,
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = roleLabel,
                                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Direct Add Button
                            Button(
                                onClick = {
                                    addPlayer(newPlayerNameInput, selectedRole)
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                                    .testTag("add_player_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Add Player to $activeTeamName (+ ઉમેરો)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 1-CLICK FAST SUGGESTIONS CHIPS
                    Text(
                        text = "⚡ ઝડપથી ઉમેરો (Suggested Players - Tap to Add):",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            Triple("Kasam Jat", "All-rounder", "Naliya"),
                            Triple("Latif Jat", "Bowler", "Naliya"),
                            Triple("Abuahemad Jat", "Batsman", "Naliya"),
                            Triple("Hasan Jat", "All-rounder", "Naliya"),
                            Triple("Imran Pathan", "All-rounder", "Bhuj"),
                            Triple("Hardik Solanki", "Bowler", "Rajkot"),
                            Triple("Suresh Jadeja", "Batsman", "Gandhidham"),
                            Triple("Tariq Baloch", "Bowler", "Bhuj"),
                            Triple("Ali Mohammad", "Batsman", "Mandvi"),
                            Triple("Rashid Khan", "Bowler", "Bhuj")
                        ).forEach { (name, role, city) ->
                            val alreadyInTeam = activeList.any { it.name.equals(name, ignoreCase = true) }
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable(enabled = !alreadyInTeam) {
                                        addPlayer(name, role, city)
                                    },
                                color = if (alreadyInTeam) Color(0xFFE2E8F0) else CricTealLight,
                                shape = RoundedCornerShape(16.dp),
                                border = if (!alreadyInTeam) CardDefaults.outlinedCardBorder().copy(width = 0.5.dp) else null
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        if (alreadyInTeam) Icons.Default.Check else Icons.Default.Add,
                                        contentDescription = null,
                                        tint = if (alreadyInTeam) Color.Gray else CricTeal,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "$name ($role)",
                                        color = if (alreadyInTeam) Color.Gray else CricTeal,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // CURRENT SQUAD LIST HEADER
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Current Squad (${activeList.size} Players) - $activeTeamName:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (activeList.isNotEmpty()) {
                            Text(
                                text = "Clear All",
                                fontSize = 12.sp,
                                color = CricSecondary,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable {
                                    if (activeSquadTab == 0) teamAPlayers = emptyList() else teamBPlayers = emptyList()
                                    successAddMessage = null
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // SQUAD PLAYERS CARD
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            if (activeList.isEmpty()) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.Groups, contentDescription = null, tint = CricTextSecondary, modifier = Modifier.size(36.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        "કોઈ ખેલાડી ઉમેર્યા નથી (No Players Added)",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "ઉપર નામ લખીને '+ Add' દબાવો અથવા ઉપરના સૂચિત ખેલાડી પર ક્લિક કરો.",
                                        fontSize = 12.sp,
                                        color = CricTextSecondary,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            } else {
                                activeList.forEachIndexed { index, player ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable { viewModel.viewPlayer(player) }
                                                .padding(vertical = 2.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(30.dp)
                                                    .clip(CircleShape)
                                                    .background(if (activeSquadTab == 0) CricSecondary.copy(alpha = 0.15f) else CricTeal.copy(alpha = 0.15f)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    text = "${index + 1}",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = if (activeSquadTab == 0) CricSecondary else CricTeal
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column {
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(
                                                        text = player.name,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 14.sp,
                                                        color = MaterialTheme.colorScheme.onSurface
                                                    )
                                                    if (player.phone.isNotBlank()) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "📞 ${player.phone}",
                                                            fontSize = 11.sp,
                                                            color = CricTeal,
                                                            fontWeight = FontWeight.SemiBold
                                                        )
                                                    }
                                                }
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Surface(
                                                        color = MaterialTheme.colorScheme.surface,
                                                        shape = RoundedCornerShape(4.dp)
                                                    ) {
                                                        Text(
                                                            text = player.role,
                                                            fontSize = 10.sp,
                                                            fontWeight = FontWeight.Medium,
                                                            color = CricTeal,
                                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                        )
                                                    }
                                                    if (player.city.isNotBlank()) {
                                                        Spacer(modifier = Modifier.width(6.dp))
                                                        Text(
                                                            text = "• ${player.city}",
                                                            fontSize = 11.sp,
                                                            color = CricTextSecondary
                                                        )
                                                    }
                                                }
                                            }
                                        }

                                        IconButton(
                                            onClick = {
                                                if (activeSquadTab == 0) {
                                                    teamAPlayers = teamAPlayers.filterNot { it.id == player.id }
                                                } else {
                                                    teamBPlayers = teamBPlayers.filterNot { it.id == player.id }
                                                }
                                                successAddMessage = null
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Default.Close,
                                                contentDescription = "Remove",
                                                tint = Color(0xFFEF4444),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                    if (index < activeList.size - 1) {
                                        HorizontalDivider(color = Color(0xFFE2E8F0).copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // STEP 1 BOTTOM BUTTONS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { currentStep = 0 },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.weight(1f).height(50.dp)
                        ) {
                            Text("Back (પાછા)")
                        }

                        Button(
                            onClick = {
                                if (battingPlayers.isNotEmpty()) {
                                    if (battingPlayers.none { it.id == selectedStriker.id }) {
                                        selectedStriker = battingPlayers[0]
                                    }
                                    if (battingPlayers.none { it.id == selectedNonStriker.id }) {
                                        selectedNonStriker = battingPlayers.getOrElse(1) { battingPlayers[0] }
                                    }
                                }
                                if (bowlingPlayers.isNotEmpty()) {
                                    if (bowlingPlayers.none { it.id == selectedOpeningBowler.id }) {
                                        selectedOpeningBowler = bowlingPlayers[0]
                                    }
                                }
                                currentStep = 2
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(2f)
                                .height(50.dp)
                                .testTag("next_to_toss_button")
                        ) {
                            Text("Next: Toss & Openers", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                // ================= STEP 2: TOSS & OPENERS (GO LIVE!) =================
                2 -> {
                    Text("ટોસ અને ઓપનિંગ પ્લેયર્સ (Toss & Openers)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = CricTeal)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text("ટોસ કોણ જીત્યું? (Who won the toss?):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { tossWinnerIndex = 0 },
                            color = if (tossWinnerIndex == 0) CricTealLight else Color(0xFFF1F5F9),
                            border = if (tossWinnerIndex == 0) CardDefaults.outlinedCardBorder().copy(width = 1.dp) else null
                        ) {
                            Text(
                                text = teamAName.ifBlank { "Team 1" },
                                fontWeight = if (tossWinnerIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                color = if (tossWinnerIndex == 0) CricTeal else CricTextPrimary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { tossWinnerIndex = 1 },
                            color = if (tossWinnerIndex == 1) CricTealLight else Color(0xFFF1F5F9),
                            border = if (tossWinnerIndex == 1) CardDefaults.outlinedCardBorder().copy(width = 1.dp) else null
                        ) {
                            Text(
                                text = teamBName.ifBlank { "Team 2" },
                                fontWeight = if (tossWinnerIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                color = if (tossWinnerIndex == 1) CricTeal else CricTextPrimary,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text("નિર્ણય (Elected to):", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Bat", "Bowl").forEach { opt ->
                            val isSel = (opt == tossDecision)
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { tossDecision = opt },
                                color = if (isSel) CricTealLight else Color(0xFFF1F5F9),
                                border = if (isSel) CardDefaults.outlinedCardBorder().copy(width = 1.dp) else null
                            ) {
                                Text(
                                    text = if (opt == "Bat") "Batting (બેટિંગ)" else "Bowling (બોલિંગ)",
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSel) CricTeal else CricTextPrimary,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Batting & Bowling banner
                    val battingTeamName = if (battingTeamIsA) teamAName else teamBName
                    val bowlingTeamName = if (battingTeamIsA) teamBName else teamAName
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("🏏 Batting First: $battingTeamName", fontWeight = FontWeight.Bold, color = Color(0xFF92400E), fontSize = 13.sp)
                            Text("🎯 Bowling First: $bowlingTeamName", fontWeight = FontWeight.Bold, color = Color(0xFF92400E), fontSize = 13.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val currentStriker = battingPlayers.firstOrNull { it.id == selectedStriker.id } ?: battingPlayers.firstOrNull() ?: Player("def_s", "Striker", "Naliya")
                    val currentNonStriker = battingPlayers.filterNot { it.id == currentStriker.id }.firstOrNull { it.id == selectedNonStriker.id }
                        ?: battingPlayers.filterNot { it.id == currentStriker.id }.firstOrNull()
                        ?: Player("def_ns", "Non-Striker", "Naliya")
                    val currentOpeningBowler = bowlingPlayers.firstOrNull { it.id == selectedOpeningBowler.id } ?: bowlingPlayers.firstOrNull() ?: Player("def_bow", "Bowler", "Bhuj")

                    // Openers Selection
                    Text("ઓપનિંગ સ્ટ્રાઈકર બેટ્સમેન (*):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        battingPlayers.forEach { p ->
                            val isSel = (p.id == currentStriker.id)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedStriker = p },
                                color = if (isSel) CricTeal else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = p.name,
                                    color = if (isSel) Color.White else CricTextPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("નોન-સ્ટ્રાઈકર બેટ્સમેન:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        battingPlayers.filterNot { it.id == currentStriker.id }.forEach { p ->
                            val isSel = (p.id == currentNonStriker.id)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedNonStriker = p },
                                color = if (isSel) CricTeal else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = p.name,
                                    color = if (isSel) Color.White else CricTextPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("ઓપનિંગ બોલર ($bowlingTeamName):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bowlingPlayers.forEach { p ->
                            val isSel = (p.id == currentOpeningBowler.id)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedOpeningBowler = p },
                                color = if (isSel) CricSecondary else Color(0xFFF1F5F9)
                            ) {
                                Text(
                                    text = p.name,
                                    color = if (isSel) Color.White else CricTextPrimary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                    fontSize = 12.sp,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // THE BIG GO LIVE BUTTON!
                    Button(
                        onClick = {
                            val teamA = Team(
                                id = "team_a_${System.currentTimeMillis()}",
                                name = teamAName.ifBlank { "Team 1" },
                                shortName = teamAName.take(3).uppercase(),
                                city = venue,
                                colorHex = 0xFFDC2626,
                                players = teamAPlayers.toList()
                            )

                            val teamB = Team(
                                id = "team_b_${System.currentTimeMillis()}",
                                name = teamBName.ifBlank { "Team 2" },
                                shortName = teamBName.take(3).uppercase(),
                                city = venue,
                                colorHex = 0xFF0D9488,
                                players = teamBPlayers.toList()
                            )

                            val tossWinner = if (tossWinnerIndex == 0) teamA else teamB

                            // Create brand new fresh match at 0/0 and launch Live Scoring screen!
                            viewModel.createAndStartMatch(
                                teamA = teamA,
                                teamB = teamB,
                                totalOvers = selectedOvers,
                                matchType = selectedMatchType,
                                ballType = selectedBallType,
                                venue = venue,
                                tossWinner = tossWinner,
                                tossDecision = tossDecision,
                                striker = currentStriker,
                                nonStriker = currentNonStriker,
                                openingBowler = currentOpeningBowler
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CricGreen),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .testTag("go_live_button")
                    ) {
                        Icon(Icons.Default.Videocam, contentDescription = null, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("🚀 Go Live & Start Scoring (મેચ લાઇવ કરો)", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun StepIndicatorTab(
    stepIndex: Int,
    title: String,
    isActive: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() },
        color = when {
            isActive -> CricTeal
            isCompleted -> CricTealLight
            else -> Color(0xFFF1F5F9)
        }
    ) {
        Row(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = CricTeal, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = title,
                fontSize = 11.sp,
                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                color = when {
                    isActive -> Color.White
                    isCompleted -> CricTeal
                    else -> CricTextSecondary
                },
                maxLines = 1
            )
        }
    }
}
