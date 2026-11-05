package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.data.PlayerMatchRecord
import com.example.data.LeaderboardItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProfile
import com.example.ui.components.UserProfileAvatar
import com.example.ui.theme.CricCardLight
import com.example.ui.theme.CricDarkCard
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricGreen
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

@Composable
fun ProfileScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    var isEditProfileDialogOpen by remember { mutableStateOf(false) }
    var isPhotoPickerChoiceOpen by remember { mutableStateOf(false) }

    // Android Zero-permission Photo Picker
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateProfilePhoto(uri.toString())
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("profile_screen")
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
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("profile_back_button")) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ક્રિકેટ પ્રોફાઇલ (Cricket Profile)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { isEditProfileDialogOpen = true },
                    modifier = Modifier.testTag("top_edit_profile_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Profile",
                        tint = CricTeal
                    )
                }
            }
        }

        // Scrollable Profile Details
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Profile Header Card (Photo + Name + Title + City + Edit Photo)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(20.dp))
                    .testTag("profile_header_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Photo with Camera badge
                    Box(
                        contentAlignment = Alignment.BottomEnd,
                        modifier = Modifier
                            .clickable { isPhotoPickerChoiceOpen = true }
                            .testTag("profile_photo_click_target")
                    ) {
                        UserProfileAvatar(
                            photoUri = userProfile.photoUri,
                            avatarPreset = userProfile.avatarPreset,
                            sizeDp = 92,
                            borderWidth = 3
                        )

                        // Camera badge
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(CricTeal)
                                .border(2.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Change Photo",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = { isPhotoPickerChoiceOpen = true },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.height(32.dp).testTag("change_photo_button")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp), tint = CricTeal)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("ફોટો બદલો (Change Photo)", fontSize = 11.sp, color = CricTeal, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // User Name
                    Text(
                        text = userProfile.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.testTag("profile_user_name")
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // User Title / Role
                    Surface(
                        color = CricTeal.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = userProfile.title,
                            color = CricTeal,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                                .testTag("profile_user_title")
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Location & Phone
                    Text(
                        text = "📍 ${userProfile.city} • 📞 ${userProfile.phone}",
                        fontSize = 12.sp,
                        color = CricTextSecondary,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Edit Profile Action Button
                    Button(
                        onClick = { isEditProfileDialogOpen = true },
                        colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .testTag("edit_profile_dialog_button")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("પ્રોફાઇલ એડિટ કરો (Edit Profile)", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Career Stats Banner: Matches, Total Runs, Wickets (Exact user requirement)
            Text(
                text = "કારકિર્દી આંકડા (Career Record)",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ProfileStatCard(
                    title = "કુલ મેચ",
                    subtitle = "Matches",
                    value = "${userProfile.matches}",
                    color = CricTeal,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "ટોટલ સ્કોર",
                    subtitle = "Runs",
                    value = "${userProfile.runs}",
                    color = CricSecondary,
                    modifier = Modifier.weight(1f)
                )
                ProfileStatCard(
                    title = "કુલ વિકેટ",
                    subtitle = "Wickets",
                    value = "${userProfile.wickets}",
                    color = CricGold,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-Navigation Tabs: Batting, Bowling, Fielding, Score History, Teams, Leaderboard (Item 12)
            var selectedTab by remember { androidx.compose.runtime.mutableIntStateOf(0) }
            val tabTitles = listOf(
                "બેટિંગ (Batting)",
                "બોલિંગ (Bowling)",
                "ફિલ્ડિંગ (Fielding)",
                "સ્કોર હિસ્ટ્રી (History)",
                "ટીમો (Teams)",
                "લીડરબોર્ડ (Leaderboard)"
            )

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = CricTeal,
                edgePadding = 0.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("profile_stat_tabs")
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 12.sp,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            when (selectedTab) {
                // ================= 0: BATTING TAB =================
                0 -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .testTag("profile_batting_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.SportsCricket, contentDescription = null, tint = CricTeal, modifier = Modifier.size(22.dp))
                                Text("બેટિંગ રેકોર્ડ (Batting Career Record)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ProfileMiniMetric("કુલ રન (Runs)", "${userProfile.runs}")
                                ProfileMiniMetric("મેચ (Matches)", "${userProfile.matches}")
                                ProfileMiniMetric("સર્વોચ્ચ (HS)", userProfile.highestScore)
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ProfileMiniMetric("સ્ટ્રાઇક રેટ (SR)", String.format("%.1f", userProfile.strikeRate))
                                ProfileMiniMetric("સરેરાશ (Avg)", String.format("%.1f", userProfile.battingAvg))
                                ProfileMiniMetric("50s / 100s", "${userProfile.fifties} / ${userProfile.hundreds}")
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ProfileMiniMetric("ચોગ્ગા (4s)", "${userProfile.fours}")
                                ProfileMiniMetric("છગ્ગા (6s)", "${userProfile.sixes}")
                                ProfileMiniMetric("બેટિંગ શૈલી", "Right-hand bat")
                            }
                        }
                    }
                }

                // ================= 1: BOWLING TAB =================
                1 -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .testTag("profile_bowling_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = CricGold, modifier = Modifier.size(22.dp))
                                Text("બોલિંગ રેકોર્ડ (Bowling Career Record)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ProfileMiniMetric("કુલ વિકેટ", "${userProfile.wickets}")
                                ProfileMiniMetric("શ્રેષ્ઠ બોલિંગ", userProfile.bestBowling)
                                ProfileMiniMetric("ઇકોનોમી (Eco)", String.format("%.1f", userProfile.bowlingEconomy))
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ProfileMiniMetric("ઓવર્સ (Overs)", String.format("%.1f", userProfile.wickets * 2.8))
                                ProfileMiniMetric("સરેરાશ (Avg)", String.format("%.1f", 16.4f))
                                ProfileMiniMetric("સ્ટ્રાઇક રેટ", String.format("%.1f", 14.8f))
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ProfileMiniMetric("3-Wkt Hauls", "6")
                                ProfileMiniMetric("5-Wkt Hauls", "1")
                                ProfileMiniMetric("બોલિંગ શૈલી", "Right-arm medium")
                            }
                        }
                    }
                }

                // ================= 2: FIELDING TAB =================
                2 -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(2.dp, RoundedCornerShape(16.dp))
                            .testTag("profile_fielding_card"),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("🧤", fontSize = 22.sp)
                                Text("ફિલ્ડિંગ રેકોર્ડ (Fielding Career Record)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                ProfileMiniMetric("કુલ કેચ (Catches)", "${userProfile.catches}")
                                ProfileMiniMetric("રન આઉટ (Run Outs)", "${userProfile.runOuts}")
                                ProfileMiniMetric("સ્ટમ્પિંગ (Stumpings)", "${userProfile.stumpings}")
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Surface(
                                color = CricTeal.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CricTeal, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text("ફિલ્ડિંગ ઇમ્પેક્ટ રેટિંગ: ઉત્કૃષ્ટ (A+)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = CricTeal)
                                        Text("પ્રતિ મેચ સરેરાશ 0.85 કેચ અને ડાયરેક્ટ હિટ રન આઉટ", fontSize = 11.sp, color = CricTextSecondary)
                                    }
                                }
                            }
                        }
                    }
                }

                // ================= 3: SCORE HISTORY TAB =================
                3 -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().testTag("profile_history_section"),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ટોટલ સ્કોર હિસ્ટ્રી (Match-by-Match History):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        if (userProfile.matchHistory.isEmpty()) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("🏏", fontSize = 32.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("હજી સુધી કોઈ મેચ હિસ્ટ્રી નથી", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text("લાઈવ મેચ પૂર્ણ થયા પછી સ્કોર હિસ્ટ્રી આપોઆપ અહીં ઉમેરાશે.", fontSize = 12.sp, color = CricTextSecondary)
                                }
                            }
                        } else {
                            userProfile.matchHistory.forEach { record ->
                                MatchHistoryItemCard(record = record)
                            }
                        }
                    }
                }

                // ================= 4: TEAMS & MATCHES TAB =================
                4 -> {
                    Column(
                        modifier = Modifier.fillMaxWidth().testTag("profile_teams_section"),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "રમેલી ટીમોની વિગતો (${userProfile.teamsRepresented.size} ટીમો):",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        userProfile.teamsRepresented.forEachIndexed { idx, teamName ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(42.dp)
                                                .clip(CircleShape)
                                                .background(if (idx == 0) CricTeal else CricSecondary),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(teamName.take(2).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(teamName, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text(if (idx == 0) "કેપ્ટન અને ઓલરાઉન્ડર • જર્સી #7" else "ઓલરાઉન્ડર • જર્સી #7", fontSize = 12.sp, color = CricTextSecondary)
                                        }
                                    }

                                    Surface(
                                        color = Color(0xFFF1F5F9),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "${(userProfile.matches / (idx + 1)).coerceAtLeast(1)} મેચ",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CricTextPrimary,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ================= 5: LEADERBOARD TAB =================
                5 -> {
                    var leaderboardCategory by remember { androidx.compose.runtime.mutableStateOf("Batting") }
                    val currentRankInfo = viewModel.getPlayerRank(userProfile.name, leaderboardCategory)

                    Column(
                        modifier = Modifier.fillMaxWidth().testTag("profile_leaderboard_section"),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Current Player Rank Banner
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text("તમારો લીડરબોર્ડ રેન્ક (Your Rank):", color = Color(0xFF94A3B8), fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = currentRankInfo.second,
                                        color = CricGold,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp
                                    )
                                    Text("Quick Heroes 11 લીડરબોર્ડ", color = Color(0xFFE2E8F0), fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .size(52.dp)
                                        .clip(CircleShape)
                                        .background(CricGold.copy(alpha = 0.2f))
                                        .border(2.dp, CricGold, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("#${currentRankInfo.first}", color = CricGold, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                                }
                            }
                        }

                        // Category Selector: Batting, Bowling, Fielding
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(
                                "Batting" to "🏏 બેટિંગ (Runs)",
                                "Bowling" to "⚡ બોલિંગ (Wickets)",
                                "Fielding" to "🧤 ફિલ્ડિંગ (Catches)"
                            ).forEach { (catKey, catLabel) ->
                                val isSel = (leaderboardCategory == catKey)
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .clickable { leaderboardCategory = catKey },
                                    color = if (isSel) CricTeal else MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = catLabel,
                                        color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                        maxLines = 1
                                    )
                                }
                            }
                        }

                        // Leaderboard Items List
                        val items = when (leaderboardCategory) {
                            "Bowling" -> viewModel.getBowlingLeaderboard(userProfile.name)
                            "Fielding" -> viewModel.getFieldingLeaderboard(userProfile.name)
                            else -> viewModel.getBattingLeaderboard(userProfile.name)
                        }

                        items.forEach { item ->
                            LeaderboardRowItem(item = item)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Grassroots Cricket Badge (100% Free - No fees)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CricGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CricGold, modifier = Modifier.size(26.dp))
                    }
                    Column {
                        Text("QUICK HEROES 11 વેરીફાઇડ ખેલાડી", color = CricGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text("100% મફત લાઈવ સ્કોરિંગ અને મેચ મેનેજમેન્ટ", color = Color(0xFF94A3B8), fontSize = 11.sp)
                    }
                }
            }
        }
    }

    // Photo Selection Choice Dialog
    if (isPhotoPickerChoiceOpen) {
        AlertDialog(
            onDismissRequest = { isPhotoPickerChoiceOpen = false },
            title = {
                Text("પ્રોફાઇલ ફોટો પસંદ કરો (Choose Photo)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("તમારી ગેલેરીમાંથી ફોટો અપલોડ કરો અથવા ક્રિકેટ અવતાર પસંદ કરો:", fontSize = 13.sp, color = CricTextSecondary)

                    // Option 1: Pick from device gallery
                    Button(
                        onClick = {
                            isPhotoPickerChoiceOpen = false
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("gallery_photo_pick_btn")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ગેલેરીમાંથી ફોટો પસંદ કરો (Pick from Gallery)")
                    }

                    Text("અથવા ક્રિકેટ અવતાર પસંદ કરો:", fontSize = 12.sp, fontWeight = FontWeight.Bold)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        PresetAvatarButton("⛑️", "Helmet", 0) {
                            viewModel.updateProfileAvatar(0)
                            isPhotoPickerChoiceOpen = false
                        }
                        PresetAvatarButton("👑", "Crown", 1) {
                            viewModel.updateProfileAvatar(1)
                            isPhotoPickerChoiceOpen = false
                        }
                        PresetAvatarButton("🏏", "Batsman", 2) {
                            viewModel.updateProfileAvatar(2)
                            isPhotoPickerChoiceOpen = false
                        }
                        PresetAvatarButton("⚡", "Bowler", 3) {
                            viewModel.updateProfileAvatar(3)
                            isPhotoPickerChoiceOpen = false
                        }
                    }

                    if (!userProfile.photoUri.isNullOrEmpty()) {
                        OutlinedButton(
                            onClick = {
                                viewModel.updateProfilePhoto(null)
                                isPhotoPickerChoiceOpen = false
                            },
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = CricSecondary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("ફોટો હટાવો (Remove Photo)")
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                OutlinedButton(onClick = { isPhotoPickerChoiceOpen = false }) {
                    Text("બંધ કરો (Close)")
                }
            }
        )
    }

    // Edit Profile Dialog (Name, Title, Matches, Total Score, Wickets, etc.)
    if (isEditProfileDialogOpen) {
        EditProfileModal(
            currentProfile = userProfile,
            onDismiss = { isEditProfileDialogOpen = false },
            onSave = { name, title, phone, city, matches, runs, wickets, highestScore, bestBowling ->
                viewModel.updateUserProfile(
                    name = name,
                    title = title,
                    phone = phone,
                    city = city,
                    matches = matches,
                    runs = runs,
                    wickets = wickets,
                    highestScore = highestScore,
                    bestBowling = bestBowling
                )
                isEditProfileDialogOpen = false
            }
        )
    }
}

@Composable
fun PresetAvatarButton(emoji: String, label: String, preset: Int, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFF1F5F9))
                .border(1.dp, CricTeal, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(emoji, fontSize = 22.sp)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(label, fontSize = 10.sp, color = CricTextSecondary)
    }
}

@Composable
fun ProfileStatCard(
    title: String,
    subtitle: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.shadow(2.dp, RoundedCornerShape(14.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = CricTextSecondary)
            Text(subtitle, fontSize = 10.sp, color = CricTextSecondary.copy(alpha = 0.7f))
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
    }
}

@Composable
fun ProfileMiniMetric(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = CricTextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun EditProfileModal(
    currentProfile: UserProfile,
    onDismiss: () -> Unit,
    onSave: (String, String, String, String, Int, Int, Int, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentProfile.name) }
    var title by remember { mutableStateOf(currentProfile.title) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var city by remember { mutableStateOf(currentProfile.city) }
    var matchesText by remember { mutableStateOf(currentProfile.matches.toString()) }
    var runsText by remember { mutableStateOf(currentProfile.runs.toString()) }
    var wicketsText by remember { mutableStateOf(currentProfile.wickets.toString()) }
    var highestScore by remember { mutableStateOf(currentProfile.highestScore) }
    var bestBowling by remember { mutableStateOf(currentProfile.bestBowling) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("પ્રોફાઇલ માહિતી સુધારો (Edit Profile)", fontWeight = FontWeight.Bold, fontSize = 17.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Name (નામ)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("નામ (Full Name)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_name_input")
                )

                // Title (ટોઇટલ / ભૂમિકા)
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("ટાઇટલ / ભૂમિકા (Role e.g. All-rounder & Captain)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_profile_title_input")
                )

                // Phone & City
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("ફોન") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("શહેર (City)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }

                HorizontalDivider(color = Color(0xFFF1F5F9))
                Text("કારકિર્દી આંકડા (Career Stats):", fontWeight = FontWeight.Bold, fontSize = 13.sp)

                // Matches, Runs, Wickets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = matchesText,
                        onValueChange = { matchesText = it },
                        label = { Text("મેચ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("edit_profile_matches_input")
                    )
                    OutlinedTextField(
                        value = runsText,
                        onValueChange = { runsText = it },
                        label = { Text("ટોટલ રન") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1.2f).testTag("edit_profile_runs_input")
                    )
                    OutlinedTextField(
                        value = wicketsText,
                        onValueChange = { wicketsText = it },
                        label = { Text("વિકેટ") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("edit_profile_wickets_input")
                    )
                }

                // Highest Score & Best Bowling
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = highestScore,
                        onValueChange = { highestScore = it },
                        label = { Text("સર્વોચ્ચ સ્કોર (HS)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = bestBowling,
                        onValueChange = { bestBowling = it },
                        label = { Text("શ્રેષ્ઠ બોલિંગ (BB)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val m = matchesText.toIntOrNull() ?: currentProfile.matches
                    val r = runsText.toIntOrNull() ?: currentProfile.runs
                    val w = wicketsText.toIntOrNull() ?: currentProfile.wickets
                    onSave(name, title, phone, city, m, r, w, highestScore, bestBowling)
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                modifier = Modifier.testTag("save_profile_button")
            ) {
                Text("સેવ કરો (Save Changes)", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("રદ કરો (Cancel)")
            }
        }
    )
}

@Composable
fun MatchHistoryItemCard(record: PlayerMatchRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = record.date,
                    fontSize = 11.sp,
                    color = CricTextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Surface(
                    color = if (record.result.contains("Won", ignoreCase = true)) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = record.result,
                        color = if (record.result.contains("Won", ignoreCase = true)) Color(0xFF166534) else Color(0xFF991B1B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = record.matchTitle,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("🏏 બેટિંગ (Batting)", fontSize = 11.sp, color = CricTextSecondary)
                    Text(
                        text = "${record.runs} રન (${record.balls} બોલ) • ${record.fours}x4, ${record.sixes}x6",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = CricTeal
                    )
                }
                Column {
                    Text("⚡ બોલિંગ (Bowling)", fontSize = 11.sp, color = CricTextSecondary)
                    Text(
                        text = "${record.wickets} વિકેટ (${record.runsConceded} રન, ${record.oversBowled} ov)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = CricGold
                    )
                }
                if (record.catches > 0 || record.runOuts > 0) {
                    Column {
                        Text("🧤 ફિલ્ડિંગ", fontSize = 11.sp, color = CricTextSecondary)
                        Text(
                            text = "${record.catches} કેચ" + if (record.runOuts > 0) ", ${record.runOuts} RO" else "",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CricSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRowItem(item: LeaderboardItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCurrentUser) CricTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
        ),
        border = if (item.isCurrentUser) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CricTeal)) else null,
        elevation = CardDefaults.cardElevation(defaultElevation = if (item.isCurrentUser) 2.dp else 0.5.dp)
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
                // Rank Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(
                            when (item.rank) {
                                1 -> CricGold
                                2 -> Color(0xFF94A3B8)
                                3 -> Color(0xFFB45309)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (item.rank) {
                            1 -> "🥇"
                            2 -> "🥈"
                            3 -> "🥉"
                            else -> "#${item.rank}"
                        },
                        fontSize = if (item.rank <= 3) 16.sp else 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.rank <= 3) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = item.playerName,
                            fontWeight = if (item.isCurrentUser) FontWeight.ExtraBold else FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (item.isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                color = CricTeal,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "તમે (You)",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "${item.teamOrCity}" + if (item.phone.isNotBlank()) " • 📞 ${item.phone}" else "",
                        fontSize = 11.sp,
                        color = CricTextSecondary
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = item.statValue,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 15.sp,
                    color = if (item.isCurrentUser) CricTeal else CricSecondary
                )
                Text(
                    text = item.statLabel,
                    fontSize = 10.sp,
                    color = CricTextSecondary
                )
            }
        }
    }
}

