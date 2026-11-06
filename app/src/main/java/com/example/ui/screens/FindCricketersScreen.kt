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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Player
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTealLight
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

@Composable
fun FindCricketersScreen(
    viewModel: CricketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val players by viewModel.players.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("Contacts (315)") }

    val filterTabs = listOf("Contacts (315)", "Popular cricketers", "Followers", "Teams")

    val filteredPlayers = players.filter {
        if (searchQuery.isBlank()) true
        else it.name.contains(searchQuery, ignoreCase = true) || it.city.contains(searchQuery, ignoreCase = true)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("find_cricketers_screen")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top App Bar (Matching Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Find cricketers",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More options",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Filter Tabs Row (Matching Screenshot 2)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterTabs.forEach { tab ->
                    val isSelected = (tab == selectedFilter)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(18.dp))
                            .clickable { selectedFilter = tab }
                            .testTag("filter_tab_$tab"),
                        color = if (isSelected) Color(0xFFCCFBF1) else MaterialTheme.colorScheme.surface,
                        border = if (isSelected) CardDefaults.outlinedCardBorder().copy(width = 1.dp) else CardDefaults.outlinedCardBorder().copy(width = 0.5.dp)
                    ) {
                        Text(
                            text = tab,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color(0xFF0F766E) else CricTextSecondary,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }
                }
            }

            // Search Bar: "Search by name" (Matching Screenshot 2)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("search_cricketers_input"),
                placeholder = {
                    Text("Search by name", color = CricTextSecondary, fontSize = 14.sp)
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

            // Cricketer List (Matching Screenshot 2)
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredPlayers, key = { it.id }) { player ->
                    CricketerListItem(
                        player = player,
                        onFollowClick = { viewModel.toggleFollow(player.id) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp)) // Space for bottom card
                }
            }
        }

        // Bottom Notification Banner (Matching Screenshot 2)
        Card(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(8.dp, RoundedCornerShape(12.dp))
                .testTag("contacts_found_banner"),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF064E3B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "We found 315 friends from your contacts who are on Quick Heroes 11. Follow them to build your network.",
                    color = Color.White,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun CricketerListItem(
    player: Player,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("player_card_${player.id}"),
        shape = RoundedCornerShape(14.dp),
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
            // Player Avatar with PRO badge if active (Screenshot 2)
            Box(contentAlignment = Alignment.TopStart) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE2E8F0)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = player.name.take(2).uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        fontSize = 18.sp
                    )
                }

                if (player.isPro) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp)),
                        color = Color(0xFF0D9488)
                    ) {
                        Text(
                            text = "STAR",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Player Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = player.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = player.city,
                    fontSize = 12.sp,
                    color = CricTextSecondary
                )
                Spacer(modifier = Modifier.height(3.dp))
                // Stats format: M: 976 | R: 19336 | W: 914
                Text(
                    text = "M: ${player.matches} | R: ${player.runs} | W: ${player.wickets}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }

            // Follow button (Matching Screenshot 2)
            OutlinedButton(
                onClick = onFollowClick,
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (player.isFollowing) CricTealLight else Color.Transparent
                ),
                border = CardDefaults.outlinedCardBorder().copy(width = 1.dp),
                modifier = Modifier
                    .height(36.dp)
                    .testTag("follow_button_${player.id}")
            ) {
                if (player.isFollowing) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = null,
                        tint = CricTeal,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Following",
                        color = CricTeal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        text = "Follow",
                        color = Color(0xFF0F766E),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
