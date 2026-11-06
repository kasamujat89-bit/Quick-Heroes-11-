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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.data.DigitalBadge
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

@Composable
fun DigitalBadgesScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val badges by viewModel.userBadges.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedBadgeForDetail by remember { mutableStateOf<DigitalBadge?>(null) }

    val filteredBadges = badges.filter {
        selectedFilter == "All" || it.category.equals(selectedFilter, ignoreCase = true)
    }

    val unlockedCount = badges.count { it.isUnlocked }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("digital_badges_screen")
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
                IconButton(onClick = onBack, modifier = Modifier.testTag("badges_back_button")) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = "Digital Badges & Honours",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = "Automatic Milestone Medals & Recognition",
                        fontSize = 11.sp,
                        color = CricTeal,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Summary Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = CricGold.copy(alpha = 0.2f),
                            modifier = Modifier.size(52.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.WorkspacePremium,
                                    contentDescription = null,
                                    tint = CricGold,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(userProfile.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text("${userProfile.city} • ${userProfile.title}", fontSize = 12.sp, color = CricTextSecondary)
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFF10B981).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$unlockedCount of ${badges.size} Honours Unlocked",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF059669),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Categories Row
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val categories = listOf("All", "Batting", "Bowling", "Fielding", "Special")
                    items(categories) { cat ->
                        FilterChip(
                            selected = selectedFilter == cat,
                            onClick = { selectedFilter = cat },
                            label = { Text(cat, fontSize = 12.sp) }
                        )
                    }
                }
            }

            // Badges List
            items(filteredBadges, key = { it.id }) { b ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedBadgeForDetail = b },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (b.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                    ),
                    border = if (b.isUnlocked) BorderStroke(1.dp, Color(b.badgeColor).copy(alpha = 0.4f)) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(b.badgeColor).copy(alpha = if (b.isUnlocked) 0.2f else 0.08f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = b.iconSymbol,
                                    fontSize = 22.sp
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = b.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (b.isUnlocked) MaterialTheme.colorScheme.onSurface else Color(0xFF94A3B8)
                                )
                                if (b.titleGujarati.isNotEmpty()) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = b.titleGujarati,
                                        fontSize = 11.sp,
                                        color = CricTextSecondary
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = b.description,
                                fontSize = 11.sp,
                                color = CricTextSecondary,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        if (b.isUnlocked) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Unlocked",
                                tint = Color(0xFF10B981),
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    // Badge Detail Modal
    if (selectedBadgeForDetail != null) {
        val b = selectedBadgeForDetail!!
        AlertDialog(
            onDismissRequest = { selectedBadgeForDetail = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(b.iconSymbol, fontSize = 26.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(b.title, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    if (b.titleGujarati.isNotEmpty()) {
                        Text(b.titleGujarati, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = CricTeal)
                        Spacer(modifier = Modifier.height(6.dp))
                    }
                    Text(b.description, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (b.isUnlocked) "Status: Unlocked and added to player profile!" else "Status: Locked. Score runs/wickets in a match to unlock automatically!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (b.isUnlocked) Color(0xFF059669) else Color(0xFFD97706)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedBadgeForDetail = null },
                    colors = ButtonDefaults.buttonColors(containerColor = CricTeal)
                ) {
                    Text("OK")
                }
            }
        )
    }
}
