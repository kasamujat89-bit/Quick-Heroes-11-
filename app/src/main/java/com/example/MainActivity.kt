package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.SportsCricket
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.ActionMenuPopup
import com.example.ui.components.CricHeroesBottomBar
import com.example.ui.components.CricHeroesTopBar
import com.example.ui.components.ProfileDrawerContent
import com.example.ui.components.PlayerDetailDialog
import com.example.ui.screens.FindCricketersScreen
import com.example.ui.screens.FullScorecardScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveScoringScreen
import com.example.ui.screens.LookingScreen
import com.example.ui.screens.MatchSetupScreen
import com.example.ui.screens.MyCricketScreen
import com.example.ui.screens.PerformanceScreen
import com.example.ui.screens.StoreScreen
import com.example.ui.screens.TournamentsScreen
import com.example.ui.theme.CricGold
import com.example.ui.theme.CricTeal
import com.example.ui.theme.CricTextPrimary
import com.example.ui.theme.CricTextSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel
import kotlinx.coroutines.launch

import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: CricketViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            MyApplicationTheme(darkTheme = isDarkMode) {
                CricHeroesApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun CricHeroesApp(
    viewModel: CricketViewModel = viewModel()
) {
    val screenStack by viewModel.screenStack.collectAsState()
    val currentScreen = screenStack.lastOrNull() ?: AppScreen.Home
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    val isPlusMenuOpen by viewModel.isPlusMenuOpen.collectAsState()
    val isHelpModalOpen by viewModel.isHelpModalOpen.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val selectedViewingPlayer by viewModel.selectedViewingPlayer.collectAsState()

    // When on Splash Screen, show logo splash screen without scaffolding
    if (currentScreen is AppScreen.Splash) {
        SplashScreen(onFinish = { viewModel.finishSplash() })
        return
    }

    // BackHandler: Handle back navigation smoothly
    BackHandler(enabled = drawerState.isOpen || screenStack.size > 1) {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            viewModel.navigateBack()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.width(320.dp)
            ) {
                ProfileDrawerContent(
                    viewModel = viewModel,
                    onCloseDrawer = {
                        coroutineScope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars),
            topBar = {
                // Show CricHeroes TopBar on main screens (Home, Looking, MyCricket)
                if (currentScreen is AppScreen.Home || currentScreen is AppScreen.Looking || currentScreen is AppScreen.MyCricket) {
                    CricHeroesTopBar(
                        onAvatarClick = {
                            coroutineScope.launch { drawerState.open() }
                        },
                        onPlusClick = {
                            viewModel.isPlusMenuOpen.value = !viewModel.isPlusMenuOpen.value
                        },
                        userProfile = userProfile,
                        isDarkMode = isDarkMode,
                        onToggleDarkMode = { viewModel.toggleDarkMode() },
                        onRefresh = { viewModel.refreshData() }
                    )
                }
            },
            bottomBar = {
                // Show Bottom Nav on main tabs (No Store)
                val isMainTab = currentScreen is AppScreen.Home ||
                        currentScreen is AppScreen.Looking ||
                        currentScreen is AppScreen.MyCricket ||
                        currentScreen is AppScreen.Community

                if (isMainTab) {
                    CricHeroesBottomBar(
                        currentScreen = currentScreen,
                        onNavigate = { viewModel.navigateTo(it) },
                        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Screen Switching
                when (currentScreen) {
                    is AppScreen.Splash -> {
                        // Handled above before Scaffold
                    }
                    is AppScreen.Home -> {
                        HomeScreen(viewModel = viewModel)
                    }
                    is AppScreen.FindCricketers, is AppScreen.Community -> {
                        FindCricketersScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is AppScreen.MyCricket -> {
                        MyCricketScreen(viewModel = viewModel)
                    }
                    is AppScreen.LiveScoring -> {
                        LiveScoringScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is AppScreen.MatchSetup -> {
                        MatchSetupScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is AppScreen.FullScorecard -> {
                        FullScorecardScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is AppScreen.Looking -> {
                        LookingScreen(viewModel = viewModel)
                    }
                    is AppScreen.Performance -> {
                        PerformanceScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is AppScreen.Tournaments -> {
                        TournamentsScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is AppScreen.Store -> {
                        StoreScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                    is AppScreen.Profile -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onBack = { viewModel.navigateBack() }
                        )
                    }
                }

                // Action Menu Dropdown (Matching Screenshot 1)
                ActionMenuPopup(
                    isOpen = isPlusMenuOpen,
                    onDismiss = { viewModel.isPlusMenuOpen.value = false },
                    onStartMatch = { viewModel.navigateTo(AppScreen.MatchSetup) },
                    onGoLive = { viewModel.navigateTo(AppScreen.MatchSetup) },
                    onAddTournament = { viewModel.navigateTo(AppScreen.Tournaments) },
                    onAddTeam = { viewModel.navigateTo(AppScreen.MatchSetup) },
                    onLookingFor = { viewModel.navigateTo(AppScreen.Looking) },
                    onGetHelp = { viewModel.isHelpModalOpen.value = true }
                )
            }
        }
    }

    // Cricket Help & Scoring Guide Dialog (100% Free - No Plan or Fees)
    if (isHelpModalOpen) {
        CricketHelpDialog(onDismiss = { viewModel.isHelpModalOpen.value = false })
    }

    // Player Details Dialog (Requirement 8 - View any player's full profile & record)
    selectedViewingPlayer?.let { player ->
        PlayerDetailDialog(
            player = player,
            onDismiss = { viewModel.closePlayerView() }
        )
    }
}

@Composable
fun CricketHelpDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.SportsCricket,
                    contentDescription = null,
                    tint = CricTeal,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Quick Heroes 11 સહાય (Help)", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "લાઈવ સ્કોરિંગ અને મેચ મેનેજમેન્ટ ગાઈડ:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))

                HelpGuideItem("1. મેચ શરૂ કરવા 'Start Match' પર ક્લિક કરો.")
                HelpGuideItem("2. ટીમ A અને B ના નામ અને ઓવર સેટ કરો.")
                HelpGuideItem("3. બંને ટીમોના ખેલાડીઓના સાચા નામ ઉમેરો.")
                HelpGuideItem("4. ટોસ, ઓપનર બેટ્સમેન અને બોલર પસંદ કરી સ્કોરિંગ શરૂ કરો.")
                HelpGuideItem("5. બોલ-બાય-બોલ કીપેડથી રન (0-6), એક્સ્ટ્રા અને વિકેટ નોંધો.")

                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ 100% મફત લાઈવ સ્કોરિંગ - કોઈપણ પ્લાન કે ચાર્જ વિના!",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF166534),
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = CricTeal),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("સમજાયું (Got it)", fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
fun HelpGuideItem(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = CricTeal, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
    }
}
