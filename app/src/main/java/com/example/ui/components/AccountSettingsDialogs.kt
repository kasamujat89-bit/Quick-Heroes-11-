package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextSecondary
import com.example.viewmodel.CricketViewModel

/**
 * Register / Login with Mobile Number Dialog
 * Allows any cricketer to register or sign in with their 10-digit mobile number, full name, city, and playing role
 */
@Composable
fun RegisterWithMobileDialog(
    viewModel: CricketViewModel,
    onDismiss: () -> Unit
) {
    val currentProfile by viewModel.userProfile.collectAsState()

    var name by remember { mutableStateOf(currentProfile.name) }
    var phone by remember { mutableStateOf(currentProfile.phone) }
    var city by remember { mutableStateOf(currentProfile.city) }
    var role by remember { mutableStateOf("All-rounder") }
    var jerseyNumber by remember { mutableStateOf("7") }

    var step by remember { mutableStateOf(1) } // 1: Enter details, 2: OTP Verification
    var otpInput by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val roles = listOf("All-rounder", "Batsman", "Bowler", "Wk-Batsman")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = CricTeal.copy(alpha = 0.12f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            tint = CricTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (step == 1) "Register / Sign In" else "Verify Mobile Number",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (step == 1) "Enter mobile number to continue" else "OTP sent to +91 $phone",
                        fontSize = 11.sp,
                        color = CricTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (step == 1) {
                    Text(
                        text = "Join Quick Heroes 11 to track match scores, maintain stats, and manage teams.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    // Mobile Number Field
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            if (it.length <= 10 && it.all { ch -> ch.isDigit() }) {
                                phone = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Mobile Number (10 digits)") },
                        leadingIcon = {
                            Text("+91 ", fontWeight = FontWeight.Bold, color = CricTeal, modifier = Modifier.padding(start = 12.dp))
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("register_phone_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Full Name Field
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Cricketer Full Name") },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = CricTeal)
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("register_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // City
                    OutlinedTextField(
                        value = city,
                        onValueChange = { city = it },
                        label = { Text("City / Town") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Role selector
                    Text("Playing Role:", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        roles.forEach { r ->
                            val isSelected = (role == r)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) CricTeal else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { role = r }
                            ) {
                                Text(
                                    text = r,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                } else {
                    // Step 2: OTP Verification
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = CricTeal.copy(alpha = 0.08f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Verification Code Sent",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = CricTeal
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Enter the 6-digit OTP sent to +91 $phone (Quick Test OTP: 112233)",
                                fontSize = 11.sp,
                                color = CricTextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = otpInput,
                        onValueChange = {
                            if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                                otpInput = it
                                errorMessage = null
                            }
                        },
                        label = { Text("Enter 6-Digit OTP") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        placeholder = { Text("112233") },
                        modifier = Modifier.fillMaxWidth().testTag("register_otp_input")
                    )

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = Color(0xFFDC2626),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (step == 1) {
                        if (phone.length < 10) {
                            errorMessage = "Please enter a valid 10-digit mobile number."
                        } else if (name.isBlank()) {
                            errorMessage = "Please enter your full name."
                        } else {
                            errorMessage = null
                            otpInput = "112233" // Auto-fill standard test OTP
                            step = 2
                        }
                    } else {
                        // Verify OTP
                        if (otpInput.length == 6) {
                            val jersey = jerseyNumber.toIntOrNull() ?: 7
                            viewModel.registerWithMobile(
                                name = name.trim(),
                                phone = phone.trim(),
                                city = city.trim(),
                                role = role,
                                jersey = jersey
                            )
                            onDismiss()
                        } else {
                            errorMessage = "Please enter a valid 6-digit OTP (e.g. 112233)."
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("register_confirm_btn")
            ) {
                Text(
                    text = if (step == 1) "Get OTP (Continue)" else "Verify & Register",
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = {
                    if (step == 2) {
                        step = 1
                    } else {
                        onDismiss()
                    }
                },
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(if (step == 2) "Back" else "Cancel")
            }
        }
    )
}

/**
 * Notification Settings Dialog (English)
 */
@Composable
fun NotificationSettingsDialog(
    viewModel: CricketViewModel,
    onDismiss: () -> Unit
) {
    val currentPrefs by viewModel.notificationSettings.collectAsState()

    var liveScore by remember { mutableStateOf(currentPrefs.liveScoreAlerts) }
    var boundary by remember { mutableStateOf(currentPrefs.boundaryAlerts) }
    var wicket by remember { mutableStateOf(currentPrefs.wicketAlerts) }
    var soundVibration by remember { mutableStateOf(currentPrefs.soundAndVibration) }
    var matchReminder by remember { mutableStateOf(currentPrefs.matchReminders) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = CricTeal.copy(alpha = 0.12f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = CricTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Notification Settings",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Live Match & Score Preferences",
                        fontSize = 11.sp,
                        color = CricTextSecondary
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Configure instant notifications and alerts:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(14.dp))

                NotificationToggleItem(
                    title = "Live Score Updates",
                    subtitle = "Real-time ball-by-ball and over updates",
                    checked = liveScore,
                    onCheckedChange = { liveScore = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                NotificationToggleItem(
                    title = "Boundaries (4s & 6s Alerts)",
                    subtitle = "Celebration screen alerts on every boundary",
                    checked = boundary,
                    onCheckedChange = { boundary = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                NotificationToggleItem(
                    title = "Wicket Alerts",
                    subtitle = "Instant alert when any player is dismissed",
                    checked = wicket,
                    onCheckedChange = { wicket = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                NotificationToggleItem(
                    title = "Sound & Vibration",
                    subtitle = "Haptic feedback on scoring and celebrations",
                    checked = soundVibration,
                    onCheckedChange = { soundVibration = it }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color(0xFFF1F5F9))

                NotificationToggleItem(
                    title = "Match Reminders",
                    subtitle = "15 minutes before scheduled tournaments & matches",
                    checked = matchReminder,
                    onCheckedChange = { matchReminder = it }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.updateNotificationSettings(
                        liveScore = liveScore,
                        boundary = boundary,
                        wicket = wicket,
                        soundVibration = soundVibration,
                        reminder = matchReminder
                    )
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_notifications_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save Preferences", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun NotificationToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = CricTextSecondary,
                lineHeight = 14.sp
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = CricTeal
            )
        )
    }
}

/**
 * Change Language Dialog (English Default)
 */
@Composable
fun ChangeLanguageDialog(
    viewModel: CricketViewModel,
    onDismiss: () -> Unit
) {
    val currentLang by viewModel.selectedLanguage.collectAsState()
    var selected by remember { mutableStateOf(currentLang) }

    val languages = listOf(
        Triple("English", "Default International Language", "en"),
        Triple("Gujarati", "Regional Language", "gu"),
        Triple("Hindi", "National Language", "hi")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF3B82F6).copy(alpha = 0.12f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = Color(0xFF2563EB),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Change Language", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Select App Display Language", fontSize = 11.sp, color = CricTextSecondary)
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Select your preferred application language:",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))

                languages.forEach { (name, desc, _) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { selected = name },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (selected == name) CricTeal.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        border = if (selected == name) CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CricTeal)) else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selected == name),
                                onClick = { selected = name },
                                colors = RadioButtonDefaults.colors(selectedColor = CricTeal)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = name,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = desc,
                                    fontSize = 11.sp,
                                    color = CricTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.setLanguage(selected)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("apply_language_btn")
            ) {
                Text("Apply Language", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Clear Data Dialog (English)
 */
@Composable
fun ClearDataDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Clear App Data?", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Clear Temporary Cache", fontSize = 11.sp, color = CricTextSecondary)
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to clear temporary cache and search filters?",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "ℹ️ Your profile details and career stats will remain completely safe.",
                        fontSize = 11.sp,
                        color = Color(0xFF92400E),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_clear_data_btn")
            ) {
                Text("Clear Data", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Delete Account Dialog (English)
 */
@Composable
fun DeleteAccountDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFFEF4444).copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = null,
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Delete Account?", fontWeight = FontWeight.Bold, fontSize = 17.sp, color = Color(0xFFDC2626))
                    Text("Permanent Removal", fontSize = 11.sp, color = CricTextSecondary)
                }
            }
        },
        text = {
            Column {
                Text(
                    text = "Are you sure you want to permanently delete your Quick Heroes 11 account?",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFFEE2E2),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Warning", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF991B1B))
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• All your matches, career stats, and profile information will be deleted.\n• This action cannot be undone.",
                            fontSize = 11.sp,
                            color = Color(0xFF991B1B),
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_delete_account_btn")
            ) {
                Text("Delete Account", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Logout Dialog (English)
 */
@Composable
fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF334155).copy(alpha = 0.12f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = null,
                            tint = Color(0xFF334155),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Logout Session?", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Sign out of Quick Heroes 11", fontSize = 11.sp, color = CricTextSecondary)
                }
            }
        },
        text = {
            Text(
                text = "Are you sure you want to log out from this device? You can sign in anytime using your mobile number.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_logout_btn")
            ) {
                Text("Logout", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Support Dialog (English)
 */
@Composable
fun SupportDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = CricTeal.copy(alpha = 0.12f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = CricTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Help & Customer Support", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Quick Heroes 11 Support", fontSize = 11.sp, color = CricTextSecondary)
                }
            }
        },
        text = {
            Column {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CricTeal.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = CricTeal, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Helpline / WhatsApp:", fontSize = 12.sp, color = CricTextSecondary)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "+91 89802 94520",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CricTeal
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• WhatsApp Support: Available 24x7\n• Location: Naliya, Kutch (Gujarat)\n• Live Scoring Assistance, Tournament Registrations, and Team Management.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Got It")
            }
        }
    )
}

/**
 * About Dialog (English)
 */
@Composable
fun AboutAppDialog(
    onDismiss: () -> Unit,
    onOpenPrivacyPolicy: (() -> Unit)? = null
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = CricGold.copy(alpha = 0.15f),
                    modifier = Modifier.size(40.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("About Quick Heroes 11", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Version 1.0.0 • 100% Free", fontSize = 11.sp, color = CricTextSecondary)
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Quick Heroes 11 is a modern live cricket scoring and match management platform built for grassroots cricketers and local tournaments.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 18.sp
                )
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CricTeal.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text("📍 Developer: Kasam Jat", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CricTeal)
                        Text("🏏 Club: Naliya Cricket Association", fontSize = 12.sp, color = CricTeal)
                        Text("📌 Location: Naliya, Kutch, Gujarat, India", fontSize = 11.sp, color = CricTextSecondary)
                        Text("📧 Contact: kasamujat89@gmail.com", fontSize = 11.sp, color = CricTextSecondary)
                        Text("📞 Helpline: +91 89802 94520", fontSize = 11.sp, color = CricTextSecondary)
                    }
                }
                Text(
                    text = "⭐ 100% Free with zero ads, zero hidden fees, and complete data privacy protection.",
                    fontSize = 11.sp,
                    color = CricTextSecondary
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Close")
            }
        },
        dismissButton = onOpenPrivacyPolicy?.let { openPolicy ->
            {
                OutlinedButton(
                    onClick = {
                        onDismiss()
                        openPolicy()
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Privacy Policy")
                }
            }
        }
    )
}

/**
 * Official Privacy Policy Dialog (Google Play Store Compliant)
 */
@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = CricTeal.copy(alpha = 0.15f),
                    modifier = Modifier.size(42.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = CricTeal,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Privacy Policy", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Text("Quick Heroes 11 • Updated Nov 2026", fontSize = 11.sp, color = CricTextSecondary)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Trust Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = CricTeal.copy(alpha = 0.08f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CricTeal, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "100% Free • No Ads • Zero Third-Party Tracking",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = CricTeal
                        )
                    }
                }

                Text(
                    text = "Quick Heroes 11 (\"we\", \"our\") is committed to protecting your privacy. This policy explains what information is collected, how it is used, and how you maintain complete ownership over your data.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                )

                HorizontalDivider(color = Color(0xFFF1F5F9))

                // Section 1
                PolicySectionItem(
                    title = "1. Information We Collect",
                    body = "• Profile Data: Player name, mobile number (for scorer identity and verification), city, player role, and jersey number.\n• Performance Data: Matches, runs, wickets, overs bowled, catches, and team scores.\n• Photos: Custom avatars selected safely through the zero-permission Android Photo Picker."
                )

                // Section 2
                PolicySectionItem(
                    title = "2. How We Use Information",
                    body = "• Real-time ball-by-ball cricket scoring and spectator live updates.\n• Saving squad players to your roster so you never have to re-enter them.\n• Displaying career stats, leaderboards, and match history."
                )

                // Section 3
                PolicySectionItem(
                    title = "3. Zero Invasive Permissions",
                    body = "• No Storage Permission required (uses system photo picker).\n• No background location or GPS tracking.\n• No device contact book access.\n• No camera hardware access without direct user request."
                )

                // Section 4
                PolicySectionItem(
                    title = "4. Data Control & Account Deletion",
                    body = "• You can permanently delete your account and profile via Account Settings -> Delete account.\n• You can clear match logs and cached rosters via Account Settings -> Clear data anytime."
                )

                // Section 5
                PolicySectionItem(
                    title = "5. No Third-Party Data Selling",
                    body = "• We DO NOT sell, rent, or share personal data with third parties or marketers.\n• No tracking pixels or behavioral ad SDKs are included."
                )

                // Section 6
                PolicySectionItem(
                    title = "6. Developer Contact & Grievance",
                    body = "• Developer: Kasam Jat & Naliya Cricket Association\n• Email: kasamujat89@gmail.com\n• Phone: +91 89802 94520\n• Address: Naliya, Kutch, Gujarat, India - 370655"
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("privacy_policy_confirm_btn")
            ) {
                Text("Understood")
            }
        }
    )
}

@Composable
fun PolicySectionItem(title: String, body: String) {
    Column {
        Text(title, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = CricTeal)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = body,
            fontSize = 11.sp,
            color = CricTextSecondary,
            lineHeight = 16.sp
        )
    }
}
