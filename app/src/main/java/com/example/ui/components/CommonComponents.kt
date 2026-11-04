package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PersonSearch
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PersonSearch
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsCricket
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.example.ui.theme.CricCardLight
import com.example.ui.theme.CricDarkCard
import com.example.ui.theme.CricDarkTextPrimary
import com.example.ui.theme.CricSecondary
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTealLight
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.AppScreen

import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.data.UserProfile
import androidx.compose.foundation.layout.fillMaxSize
import com.example.ui.theme.CricGold

@Composable
fun UserProfileAvatar(
    photoUri: String? = null,
    avatarPreset: Int = 0,
    modifier: Modifier = Modifier,
    sizeDp: Int = 40,
    borderWidth: Int = 2,
    onClick: (() -> Unit)? = null
) {
    Box(
        modifier = modifier
            .size(sizeDp.dp)
            .clip(CircleShape)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier)
            .background(Color(0xFF1E293B))
            .border(borderWidth.dp, Color(0xFFF1F5F9), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUri.isNullOrEmpty()) {
            AsyncImage(
                model = photoUri,
                contentDescription = "User Profile Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            when (avatarPreset) {
                1 -> {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Trophy Avatar",
                        tint = CricGold,
                        modifier = Modifier.size((sizeDp * 0.6).dp)
                    )
                }
                2 -> {
                    Icon(
                        imageVector = Icons.Default.SportsCricket,
                        contentDescription = "Cricket Avatar",
                        tint = Color.White,
                        modifier = Modifier.size((sizeDp * 0.6).dp)
                    )
                }
                3 -> {
                    Icon(
                        imageVector = Icons.Default.PlayCircle,
                        contentDescription = "Bowler Avatar",
                        tint = CricTeal,
                        modifier = Modifier.size((sizeDp * 0.6).dp)
                    )
                }
                else -> {
                    // Stylized cricket helmet face
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size((sizeDp * 0.55).dp)
                                .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                                .background(Color(0xFFDC2626))
                        )
                        Box(
                            modifier = Modifier
                                .width((sizeDp * 0.65).dp)
                                .height(3.dp)
                                .background(Color(0xFFFBBF24))
                        )
                        Row(
                            modifier = Modifier.padding(top = 1.dp),
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Box(modifier = Modifier.width(2.dp).height(4.dp).background(Color(0xFFE2E8F0)))
                            Box(modifier = Modifier.width(2.dp).height(4.dp).background(Color(0xFFE2E8F0)))
                            Box(modifier = Modifier.width(2.dp).height(4.dp).background(Color(0xFFE2E8F0)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CricHelmetAvatar(
    modifier: Modifier = Modifier,
    sizeDp: Int = 40,
    borderWidth: Int = 2
) {
    UserProfileAvatar(
        photoUri = null,
        avatarPreset = 0,
        modifier = modifier,
        sizeDp = sizeDp,
        borderWidth = borderWidth
    )
}

@Composable
fun CricHeroesTopBar(
    onAvatarClick: () -> Unit,
    onPlusClick: () -> Unit,
    modifier: Modifier = Modifier,
    userProfile: UserProfile? = null,
    onProClick: (() -> Unit)? = null
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: User Avatar & Quick Heroes 11 brand
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onAvatarClick() }
                    .testTag("avatar_button")
            ) {
                UserProfileAvatar(
                    photoUri = userProfile?.photoUri,
                    avatarPreset = userProfile?.avatarPreset ?: 0,
                    sizeDp = 42
                )

                Column {
                    Text(
                        text = userProfile?.name ?: "Kasam Jat",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CricTextPrimary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SportsCricket,
                            contentDescription = null,
                            tint = CricTeal,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Quick Heroes 11",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            color = CricTeal
                        )
                    }
                }
            }

            // Right: Chat, Bell, and CricHeroes Green/Teal '+' Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                IconButton(
                    onClick = { /* Chat dialog */ },
                    modifier = Modifier.size(38.dp).testTag("chat_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Chat,
                        contentDescription = "Chats",
                        tint = CricTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(
                    onClick = { /* Notifications */ },
                    modifier = Modifier.size(38.dp).testTag("notifications_icon_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = CricTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Green/Teal '+' Button (Screenshot 1)
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(CricTeal)
                        .clickable { onPlusClick() }
                        .testTag("plus_fab_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Start match or action",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionMenuPopup(
    isOpen: Boolean,
    onDismiss: () -> Unit,
    onStartMatch: () -> Unit,
    onGoLive: () -> Unit,
    onAddTournament: () -> Unit,
    onAddTeam: () -> Unit,
    onLookingFor: () -> Unit,
    onGetHelp: () -> Unit
) {
    if (!isOpen) return

    Popup(
        alignment = Alignment.TopEnd,
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        Card(
            modifier = Modifier
                .padding(top = 56.dp, end = 14.dp)
                .width(220.dp)
                .shadow(12.dp, RoundedCornerShape(14.dp))
                .testTag("action_menu_popup"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                ActionMenuItem(
                    icon = Icons.Default.SportsCricket,
                    title = "Start a match",
                    onClick = {
                        onDismiss()
                        onStartMatch()
                    }
                )
                ActionMenuItem(
                    icon = Icons.Default.Videocam,
                    title = "Go live",
                    onClick = {
                        onDismiss()
                        onGoLive()
                    }
                )
                ActionMenuItem(
                    icon = Icons.Default.EmojiEvents,
                    title = "Add a tournament / series",
                    onClick = {
                        onDismiss()
                        onAddTournament()
                    }
                )
                ActionMenuItem(
                    icon = Icons.Default.GroupAdd,
                    title = "Add team",
                    onClick = {
                        onDismiss()
                        onAddTeam()
                    }
                )
                ActionMenuItem(
                    icon = Icons.Default.PersonSearch,
                    title = "Looking for",
                    onClick = {
                        onDismiss()
                        onLookingFor()
                    }
                )
                HorizontalDivider(color = Color(0xFFF1F5F9), modifier = Modifier.padding(vertical = 4.dp))
                ActionMenuItem(
                    icon = Icons.AutoMirrored.Filled.HelpOutline,
                    title = "Get help",
                    onClick = {
                        onDismiss()
                        onGetHelp()
                    }
                )
            }
        }
    }
}

@Composable
private fun ActionMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = CricTextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun CricHeroesBottomBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = currentScreen is AppScreen.Home,
            onClick = { onNavigate(AppScreen.Home) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is AppScreen.Home) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CricSecondary,
                selectedTextColor = CricSecondary,
                indicatorColor = Color(0xFFFFE4E6),
                unselectedIconColor = CricTextSecondary,
                unselectedTextColor = CricTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = currentScreen is AppScreen.Looking,
            onClick = { onNavigate(AppScreen.Looking) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is AppScreen.Looking) Icons.Filled.PersonSearch else Icons.Outlined.PersonSearch,
                    contentDescription = "Looking"
                )
            },
            label = { Text("Looking", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CricSecondary,
                selectedTextColor = CricSecondary,
                indicatorColor = Color(0xFFFFE4E6),
                unselectedIconColor = CricTextSecondary,
                unselectedTextColor = CricTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_looking")
        )

        NavigationBarItem(
            selected = currentScreen is AppScreen.MyCricket,
            onClick = { onNavigate(AppScreen.MyCricket) },
            icon = {
                Icon(
                    imageVector = Icons.Filled.SportsCricket,
                    contentDescription = "My Cricket"
                )
            },
            label = { Text("My Cricket", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CricSecondary,
                selectedTextColor = CricSecondary,
                indicatorColor = Color(0xFFFFE4E6),
                unselectedIconColor = CricTextSecondary,
                unselectedTextColor = CricTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_my_cricket")
        )

        NavigationBarItem(
            selected = currentScreen is AppScreen.FindCricketers || currentScreen is AppScreen.Community,
            onClick = { onNavigate(AppScreen.FindCricketers) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Community"
                )
            },
            label = { Text("Community", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CricSecondary,
                selectedTextColor = CricSecondary,
                indicatorColor = Color(0xFFFFE4E6),
                unselectedIconColor = CricTextSecondary,
                unselectedTextColor = CricTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_community")
        )

        NavigationBarItem(
            selected = currentScreen is AppScreen.Store,
            onClick = { onNavigate(AppScreen.Store) },
            icon = {
                Icon(
                    imageVector = if (currentScreen is AppScreen.Store) Icons.Filled.ShoppingBag else Icons.Outlined.ShoppingBag,
                    contentDescription = "Store"
                )
            },
            label = { Text("Store", fontSize = 11.sp, fontWeight = FontWeight.SemiBold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CricSecondary,
                selectedTextColor = CricSecondary,
                indicatorColor = Color(0xFFFFE4E6),
                unselectedIconColor = CricTextSecondary,
                unselectedTextColor = CricTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_store")
        )
    }
}
