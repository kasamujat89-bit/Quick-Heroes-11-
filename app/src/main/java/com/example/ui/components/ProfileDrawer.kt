package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Switch
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.CricGold
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun ProfileDrawerContent(
    viewModel: CricketViewModel,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
            .testTag("profile_drawer_content")
    ) {
        // User Header with Avatar, Name, Title, and Career Highlights
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clickable {
                    onCloseDrawer()
                    viewModel.navigateTo(AppScreen.Profile)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserProfileAvatar(
                photoUri = userProfile.photoUri,
                avatarPreset = userProfile.avatarPreset,
                sizeDp = 64,
                borderWidth = 2
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userProfile.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = userProfile.phone,
                    fontSize = 12.sp,
                    color = CricTextSecondary
                )
                Spacer(modifier = Modifier.height(3.dp))
                // Career record pill
                Surface(
                    color = CricTeal.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text(
                        text = "🏏 ${userProfile.matches} મેચ • ${userProfile.runs} રન • ${userProfile.wickets} વિકેટ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = CricTeal,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Edit icon -> Opens ProfileScreen
            IconButton(
                onClick = {
                    onCloseDrawer()
                    viewModel.navigateTo(AppScreen.Profile)
                },
                modifier = Modifier.size(36.dp).testTag("drawer_edit_profile_icon")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    tint = CricTeal,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Heroes 11 Club Card (100% Free, No Plan)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("drawer_brand_banner"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black,
                    modifier = Modifier.size(46.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.app_logo),
                        contentDescription = "Quick Heroes 11",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                }
                Column {
                    Text(
                        text = "QUICK HEROES 11",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CricGold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "100% ફ્રી લાઈવ ક્રિકેટ સ્કોરિંગ",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))

        // Menu Items matching Screenshot 3
        DrawerMenuItem(
            icon = Icons.Default.Info,
            title = "What's New",
            onClick = {
                onCloseDrawer()
            }
        )

        DrawerMenuItem(
            icon = Icons.Default.SportsCricket,
            title = "Your Cricket Profile (પ્રોફાઇલ)",
            onClick = {
                onCloseDrawer()
                viewModel.navigateTo(AppScreen.Profile)
            }
        )

        DrawerMenuItem(
            icon = Icons.Default.BarChart,
            title = "My Performance",
            onClick = {
                onCloseDrawer()
                viewModel.navigateTo(AppScreen.Performance)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9))

        DrawerMenuItem(
            icon = Icons.Default.Leaderboard,
            title = "Leaderboards",
            onClick = {
                onCloseDrawer()
                viewModel.navigateTo(AppScreen.Tournaments)
            }
        )

        DrawerMenuItem(
            icon = Icons.Default.EmojiEvents,
            title = "Quick Heroes Awards",
            onClick = {
                onCloseDrawer()
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9))

        DrawerMenuItem(
            icon = Icons.Default.MilitaryTech,
            title = "Associations",
            onClick = {
                onCloseDrawer()
            }
        )

        DrawerMenuItem(
            icon = Icons.Default.AccountBalance,
            title = "Clubs",
            onClick = {
                onCloseDrawer()
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // Dark Mode / Light Mode Switch (Requirements 8 & 12)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleDarkMode() }
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFFFBBF24) else CricTeal,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = if (isDarkMode) "ડાર્ક મોડ (Dark Mode)" else "લાઇટ મોડ (Light Mode)",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isDarkMode) "ડાર્ક થીમ સક્રિય છે" else "ડિફોલ્ટ લાઇટ થીમ",
                        fontSize = 11.sp,
                        color = CricTextSecondary
                    )
                }
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = { viewModel.toggleDarkMode() }
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF334155),
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
