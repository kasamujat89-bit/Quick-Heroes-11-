package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel

@Composable
fun ProfileDrawerContent(
    viewModel: CricketViewModel,
    onCloseDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val selectedLanguage by viewModel.selectedLanguage.collectAsState()

    var isAccountSettingsExpanded by remember { mutableStateOf(true) }
    var isSupportExpanded by remember { mutableStateOf(false) }
    var isOthersExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(MaterialTheme.colorScheme.surface)
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp)
            .testTag("profile_drawer_content")
    ) {
        // User Header with Avatar, Name, Phone & Career Highlights
        if (isLoggedIn) {
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
                    sizeDp = 60,
                    borderWidth = 2
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = userProfile.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (userProfile.phone.isNotBlank()) {
                        Text(
                            text = "+91 ${userProfile.phone}",
                            fontSize = 12.sp,
                            color = CricTextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Surface(
                        color = CricTeal.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "🏏 ${userProfile.matches} M • ${userProfile.runs} R • ${userProfile.wickets} W",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = CricTeal,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

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
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        } else {
            // Logged Out Banner: Prompts user to register or sign in with mobile number
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = CricTeal.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Register With Mobile Number", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("Create cricketer profile and save career statistics", fontSize = 11.sp, color = CricTextSecondary)
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            onCloseDrawer()
                            viewModel.isRegisterDialogOpen.value = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().testTag("drawer_register_mobile_btn")
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Register / Sign In", fontWeight = FontWeight.Bold)
                    }
                }
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
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.Black,
                    modifier = Modifier.size(42.dp)
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
                        fontSize = 13.sp,
                        color = CricGold
                    )
                    Text(
                        text = "100% Free Live Cricket Scoring",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 1. Clubs & Free Tournaments ---
        DrawerMenuItem(
            icon = Icons.Default.AccountBalance,
            title = "Clubs & Tournaments",
            subtitle = "100% Free",
            onClick = {
                onCloseDrawer()
                viewModel.navigateTo(AppScreen.Tournaments)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 2. Local Fantasy League (Requirement 2) ---
        DrawerMenuItem(
            icon = Icons.Default.EmojiEvents,
            title = "Local Fantasy League",
            subtitle = "100% Free",
            onClick = {
                onCloseDrawer()
                viewModel.navigateTo(AppScreen.LocalFantasy)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 3. Digital Badges & Honours (Requirement 3) ---
        DrawerMenuItem(
            icon = Icons.Default.WorkspacePremium,
            title = "Digital Badges & Honours",
            subtitle = "Medals",
            onClick = {
                onCloseDrawer()
                viewModel.navigateTo(AppScreen.DigitalBadges)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 4. Player Marketplace (Requirement 4) ---
        DrawerMenuItem(
            icon = Icons.Default.ElectricBolt,
            title = "Player Marketplace",
            subtitle = "Urgent",
            onClick = {
                onCloseDrawer()
                viewModel.navigateTo(AppScreen.Looking)
            }
        )

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 2. Appearance (Dark / Light Mode) ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleDarkMode() }
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("drawer_appearance_item"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Palette,
                    contentDescription = null,
                    tint = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFF334155),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Appearance",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isDarkMode) "Dark Mode" else "Light Mode",
                        fontSize = 11.sp,
                        color = CricTextSecondary
                    )
                }
            }
            Switch(
                checked = isDarkMode,
                onCheckedChange = { viewModel.toggleDarkMode() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = CricTeal
                )
            )
        }

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 3. Support (Expandable) ---
        ExpandableDrawerHeader(
            icon = Icons.Default.SupportAgent,
            title = "Support",
            isExpanded = isSupportExpanded,
            onToggle = { isSupportExpanded = !isSupportExpanded }
        )

        AnimatedVisibility(
            visible = isSupportExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
                DrawerSubMenuItem(
                    icon = Icons.Default.Phone,
                    title = "Call Support (+91 89802 94520)",
                    onClick = {
                        onCloseDrawer()
                        viewModel.isSupportDialogOpen.value = true
                    }
                )
                DrawerSubMenuItem(
                    icon = Icons.Default.HelpOutline,
                    title = "Scoring Help & FAQs",
                    onClick = {
                        onCloseDrawer()
                        viewModel.isHelpModalOpen.value = true
                    }
                )
            }
        }

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 4. Others (Expandable) ---
        ExpandableDrawerHeader(
            icon = Icons.Default.Info,
            title = "Others",
            isExpanded = isOthersExpanded,
            onToggle = { isOthersExpanded = !isOthersExpanded }
        )

        AnimatedVisibility(
            visible = isOthersExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))) {
                DrawerSubMenuItem(
                    icon = Icons.Default.Info,
                    title = "About Quick Heroes 11",
                    onClick = {
                        onCloseDrawer()
                        viewModel.isAboutDialogOpen.value = true
                    }
                )
                DrawerSubMenuItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    onClick = {
                        onCloseDrawer()
                        viewModel.isPrivacyPolicyDialogOpen.value = true
                    },
                    testTag = "menu_others_privacy_policy"
                )
                DrawerSubMenuItem(
                    icon = Icons.Default.Share,
                    title = "Share App With Cricketers",
                    onClick = {
                        onCloseDrawer()
                        viewModel.showToast("Quick Heroes 11 link copied to share!")
                    }
                )
            }
        }

        HorizontalDivider(color = Color(0xFFF1F5F9))

        // --- 5. Account Settings (Expandable - Purchase History & App Code Deleted) ---
        ExpandableDrawerHeader(
            icon = Icons.Default.AccountBalance,
            title = "Account Settings",
            isExpanded = isAccountSettingsExpanded,
            onToggle = { isAccountSettingsExpanded = !isAccountSettingsExpanded },
            modifier = Modifier.testTag("drawer_account_settings_header")
        )

        AnimatedVisibility(
            visible = isAccountSettingsExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f))
            ) {
                // 1. Register / Mobile Number Setting
                DrawerSubMenuItem(
                    icon = Icons.Default.AppRegistration,
                    title = if (isLoggedIn) "Register / Change Mobile" else "Register With Mobile",
                    subtitle = if (isLoggedIn && userProfile.phone.isNotBlank()) "+91 ${userProfile.phone}" else null,
                    onClick = {
                        onCloseDrawer()
                        viewModel.isRegisterDialogOpen.value = true
                    },
                    testTag = "menu_register_mobile"
                )

                // 2. Notification Settings
                DrawerSubMenuItem(
                    icon = Icons.Default.Notifications,
                    title = "Notification Settings",
                    onClick = {
                        onCloseDrawer()
                        viewModel.isNotificationDialogOpen.value = true
                    },
                    testTag = "menu_notification_settings"
                )

                // 3. Change Language (Default English)
                DrawerSubMenuItem(
                    icon = Icons.Default.Translate,
                    title = "Change Language",
                    subtitle = selectedLanguage.split(" ").firstOrNull(),
                    onClick = {
                        onCloseDrawer()
                        viewModel.isLanguageDialogOpen.value = true
                    },
                    testTag = "menu_change_language"
                )

                // 4. Privacy Policy
                DrawerSubMenuItem(
                    icon = Icons.Default.Security,
                    title = "Privacy Policy",
                    subtitle = "Data security & terms",
                    onClick = {
                        onCloseDrawer()
                        viewModel.isPrivacyPolicyDialogOpen.value = true
                    },
                    testTag = "menu_account_privacy_policy"
                )

                // 5. Clear data
                DrawerSubMenuItem(
                    icon = Icons.Default.Storage,
                    title = "Clear data",
                    onClick = {
                        onCloseDrawer()
                        viewModel.isClearDataDialogOpen.value = true
                    },
                    testTag = "menu_clear_data"
                )

                // 5. Delete account
                DrawerSubMenuItem(
                    icon = Icons.Default.DeleteForever,
                    title = "Delete account",
                    textColor = Color(0xFFDC2626),
                    iconColor = Color(0xFFDC2626),
                    onClick = {
                        onCloseDrawer()
                        viewModel.isDeleteAccountDialogOpen.value = true
                    },
                    testTag = "menu_delete_account"
                )

                // 6. Logout
                if (isLoggedIn) {
                    DrawerSubMenuItem(
                        icon = Icons.Default.Logout,
                        title = "Logout",
                        textColor = Color(0xFFDC2626),
                        iconColor = Color(0xFFDC2626),
                        onClick = {
                            onCloseDrawer()
                            viewModel.isLogoutDialogOpen.value = true
                        },
                        testTag = "menu_logout"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun ExpandableDrawerHeader(
    icon: ImageVector,
    title: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
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
        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = Color(0xFF64748B),
            modifier = Modifier.size(22.dp)
        )
    }
}

@Composable
fun DrawerMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
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
        if (subtitle != null) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF10B981).copy(alpha = 0.15f)
            ) {
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF059669),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
fun DrawerSubMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    iconColor: Color = Color(0xFF475569),
    onClick: () -> Unit,
    testTag: String? = null
) {
    val modifier = Modifier
        .fillMaxWidth()
        .clickable { onClick() }
        .padding(start = 36.dp, end = 20.dp, top = 11.dp, bottom = 11.dp)

    Row(
        modifier = if (testTag != null) modifier.testTag(testTag) else modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
        if (subtitle != null) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = CricTeal.copy(alpha = 0.1f)
            ) {
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = CricTeal,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}
