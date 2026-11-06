package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LookingItem
import com.example.data.PlayerRequirement
import com.example.data.SampleData
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricGreen
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

@Composable
fun LookingScreen(
    viewModel: CricketViewModel,
    modifier: Modifier = Modifier
) {
    val playerRequirements by viewModel.playerRequirements.collectAsState()
    val isPostDialogOpen by viewModel.isPostRequirementDialogOpen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var activeTab by remember { mutableIntStateOf(0) } // 0: Player Marketplace, 1: General Community
    val categories = listOf("All", "Player", "Team", "Ground", "Umpire")
    var selectedCategory by remember { mutableStateOf("All") }

    val posts = SampleData.lookingPosts.filter {
        selectedCategory == "All" || it.category.equals(selectedCategory, ignoreCase = true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("looking_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 1.dp
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)) {
                    Text(
                        text = "Player Marketplace & Community",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Urgent player requirements, open match slots, and local community needs",
                        fontSize = 11.sp,
                        color = CricTextSecondary
                    )
                }
            }

            // Tabs: Urgent Marketplace vs General Looking
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Urgent Players (${playerRequirements.size})", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    }
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = { Text("Community Looking", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            if (activeTab == 0) {
                // Tab 0: Urgent Player Marketplace
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                            border = BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = Color(0xFFEF4444),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("Teams Urgently Seeking Players", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF991B1B))
                                    Text("Are you free to play? Connect directly with captains or tap 'Request to Join'.", fontSize = 11.sp, color = Color(0xFFB91C1C))
                                }
                            }
                        }
                    }

                    items(playerRequirements, key = { it.id }) { req ->
                        PlayerRequirementCard(
                            requirement = req,
                            onApply = { viewModel.applyForRequirement(req.id) },
                            onCall = { viewModel.showToast("Contacting Captain on +91 ${req.contactPhone}") }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            } else {
                // Tab 1: General Looking
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { cat ->
                            val isSel = (cat == selectedCategory)
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(16.dp))
                                    .clickable { selectedCategory = cat },
                                color = if (isSel) CricTeal else MaterialTheme.colorScheme.surface,
                                border = if (isSel) null else BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Text(
                                    text = cat,
                                    color = if (isSel) Color.White else CricTextSecondary,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                                )
                            }
                        }
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .padding(horizontal = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(posts, key = { it.id }) { post ->
                            LookingItemCard(item = post)
                        }
                        item {
                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }

        // Post Requirement FAB
        FloatingActionButton(
            onClick = { viewModel.isPostRequirementDialogOpen.value = true },
            containerColor = CricTeal,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("post_requirement_fab")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Post Need", fontWeight = FontWeight.Bold)
            }
        }
    }

    // Post Player Requirement Dialog
    if (isPostDialogOpen) {
        var teamName by remember { mutableStateOf("${userProfile.name}'s XI") }
        var roleNeeded by remember { mutableStateOf("Fast Bowler") }
        var matchDate by remember { mutableStateOf("Tomorrow • 7:30 AM") }
        var groundCity by remember { mutableStateOf(userProfile.city) }
        var contactPhone by remember { mutableStateOf(userProfile.phone) }
        var oversType by remember { mutableStateOf("20 Overs Leather Ball") }

        AlertDialog(
            onDismissRequest = { viewModel.isPostRequirementDialogOpen.value = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Post Urgent Player Need", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Looking for a player for your upcoming match?", fontSize = 12.sp, color = CricTextSecondary)
                    OutlinedTextField(
                        value = teamName,
                        onValueChange = { teamName = it },
                        label = { Text("Team Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = roleNeeded,
                        onValueChange = { roleNeeded = it },
                        label = { Text("Role Needed (e.g. Fast Bowler, Keeper)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = matchDate,
                        onValueChange = { matchDate = it },
                        label = { Text("Match Date & Time") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = groundCity,
                        onValueChange = { groundCity = it },
                        label = { Text("Ground / Location") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = contactPhone,
                        onValueChange = { contactPhone = it },
                        label = { Text("Captain Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.postPlayerRequirement(
                            team = teamName,
                            role = roleNeeded,
                            date = matchDate,
                            ground = groundCity,
                            phone = contactPhone,
                            overs = oversType
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = CricTeal)
                ) {
                    Text("Post Requirement", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { viewModel.isPostRequirementDialogOpen.value = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun PlayerRequirementCard(
    requirement: PlayerRequirement,
    onApply: () -> Unit,
    onCall: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ElectricBolt, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "URGENT: ${requirement.roleNeeded.uppercase()}",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 11.sp,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
                Text(requirement.timeAgo, fontSize = 11.sp, color = CricTextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${requirement.teamName} needs a ${requirement.roleNeeded}",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "📅 ${requirement.matchDate} • 🏟️ ${requirement.groundCity}",
                fontSize = 12.sp,
                color = CricTextSecondary
            )
            Text(
                text = "Format: ${requirement.oversType} • Posted by ${requirement.postedBy}",
                fontSize = 11.sp,
                color = CricTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${requirement.applicantsCount} players requested",
                    fontSize = 11.sp,
                    color = CricTeal,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onCall,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(13.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = onApply,
                        enabled = !requirement.isApplied,
                        colors = ButtonDefaults.buttonColors(containerColor = if (requirement.isApplied) Color(0xFF10B981) else CricTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        if (requirement.isApplied) {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Requested", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Text("Request to Join", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LookingItemCard(item: LookingItem) {
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
                Surface(
                    color = when (item.category) {
                        "Player" -> Color(0xFFEFF6FF)
                        "Ground" -> Color(0xFFECFDF5)
                        "Umpire" -> Color(0xFFFEF3C7)
                        else -> Color(0xFFFDF2F8)
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = item.category.uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when (item.category) {
                            "Player" -> Color(0xFF2563EB)
                            "Ground" -> CricGreen
                            "Umpire" -> Color(0xFFD97706)
                            else -> CricSecondary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(item.timeAgo, fontSize = 11.sp, color = CricTextSecondary)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.subtitle,
                fontSize = 12.sp,
                color = CricTextSecondary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = CricTextSecondary, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(item.location, fontSize = 11.sp, color = CricTextSecondary)
                }

                Button(
                    onClick = { /* Contact */ },
                    colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Call", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
