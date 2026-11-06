package com.example.viewmodel

import androidx.lifecycle.ViewModel
import com.example.data.BallEvent
import com.example.data.BatsmanScore
import com.example.data.BowlerScore
import com.example.data.CricketMatch
import com.example.data.DismissalType
import com.example.data.ExtraType
import com.example.data.ExtrasBreakdown
import com.example.data.Inning
import com.example.data.MatchStatus
import com.example.data.Player
import com.example.data.SampleData
import com.example.data.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import androidx.lifecycle.viewModelScope
import com.example.data.CelebrationType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.data.UserProfile
import com.example.data.PlayerMatchRecord
import com.example.data.LeaderboardItem
import com.example.data.NotificationPreferences
import com.example.data.DigitalBadge
import com.example.data.FantasyPlayer
import com.example.data.FantasyTeam
import com.example.data.FantasyLeaderboardEntry
import com.example.data.PlayerRequirement
import com.example.data.CommentaryItem
import com.example.data.OfflineScoringEvent

sealed class AppScreen {
    object Splash : AppScreen()
    object Home : AppScreen()
    object LiveScoring : AppScreen()
    object MatchSetup : AppScreen()
    object FindCricketers : AppScreen()
    object Looking : AppScreen()
    object MyCricket : AppScreen()
    object Community : AppScreen()
    object Store : AppScreen()
    object Performance : AppScreen()
    object Tournaments : AppScreen()
    object FullScorecard : AppScreen()
    object Profile : AppScreen()
    object LocalFantasy : AppScreen()
    object DigitalBadges : AppScreen()
}

class CricketViewModel : ViewModel() {

    // User Profile State (Editable Name, Title, Photo, Matches, Runs, Wickets, etc.)
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    // Light / Dark Mode Toggle (Default is FALSE: Light Mode as requested by user)
    val isDarkMode = MutableStateFlow(false)
    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    // Celebration Animation Event (Triggered on 4 runs, 6 runs, or wicket)
    private val _celebrationEvent = MutableStateFlow<CelebrationType?>(null)
    val celebrationEvent: StateFlow<CelebrationType?> = _celebrationEvent.asStateFlow()

    fun triggerCelebration(type: CelebrationType) {
        _celebrationEvent.value = type
    }

    fun clearCelebration() {
        _celebrationEvent.value = null
    }

    // 1. Offline Scoring System (Offline Sync)
    val isOfflineMode = MutableStateFlow(false)
    private val _pendingOfflineEvents = MutableStateFlow<List<OfflineScoringEvent>>(emptyList())
    val pendingOfflineEvents: StateFlow<List<OfflineScoringEvent>> = _pendingOfflineEvents.asStateFlow()
    val isSyncing = MutableStateFlow(false)

    fun toggleOfflineMode() {
        isOfflineMode.value = !isOfflineMode.value
        if (isOfflineMode.value) {
            showToast("Offline Ground Mode Active! You can record balls without internet.")
        } else {
            if (_pendingOfflineEvents.value.isNotEmpty()) {
                syncOfflineData()
            } else {
                showToast("Online Mode Active • Connected to server")
            }
        }
    }

    fun syncOfflineData() {
        if (_pendingOfflineEvents.value.isEmpty()) {
            showToast("All scores are already up to date with cloud!")
            return
        }
        viewModelScope.launch {
            isSyncing.value = true
            delay(800)
            val count = _pendingOfflineEvents.value.size
            _pendingOfflineEvents.value = emptyList()
            isOfflineMode.value = false
            isSyncing.value = false
            showToast("Successfully synced $count offline ball events with cloud server!")
        }
    }

    // 2. Digital Badges & Medals System (Requirement)
    private val _userBadges = MutableStateFlow(SampleData.standardBadges)
    val userBadges: StateFlow<List<DigitalBadge>> = _userBadges.asStateFlow()

    fun awardBadge(badgeId: String) {
        _userBadges.update { list ->
            list.map { b ->
                if (b.id == badgeId && !b.isUnlocked) {
                    showToast("New Badge Unlocked: ${b.title} ${b.iconSymbol}!")
                    b.copy(isUnlocked = true, dateUnlocked = "Just unlocked")
                } else b
            }
        }
    }

    // 3. 100% Free Local Fantasy League (Requirement)
    private val _userFantasyTeam = MutableStateFlow(FantasyTeam())
    val userFantasyTeam: StateFlow<FantasyTeam> = _userFantasyTeam.asStateFlow()

    private val _fantasyLeaderboard = MutableStateFlow(SampleData.sampleFantasyLeaderboard)
    val fantasyLeaderboard: StateFlow<List<FantasyLeaderboardEntry>> = _fantasyLeaderboard.asStateFlow()

    fun toggleFantasyPlayer(playerId: String) {
        val current = _userFantasyTeam.value.selectedPlayerIds
        val updated = if (current.contains(playerId)) {
            current - playerId
        } else {
            if (current.size >= 11) {
                showToast("Fantasy team can have maximum 11 players!")
                return
            }
            current + playerId
        }
        _userFantasyTeam.update {
            it.copy(
                selectedPlayerIds = updated,
                captainId = if (it.captainId == playerId && !updated.contains(playerId)) null else it.captainId,
                viceCaptainId = if (it.viceCaptainId == playerId && !updated.contains(playerId)) null else it.viceCaptainId
            )
        }
    }

    fun setFantasyCaptain(playerId: String) {
        if (!_userFantasyTeam.value.selectedPlayerIds.contains(playerId)) {
            showToast("Add player to your 11 first!")
            return
        }
        _userFantasyTeam.update {
            it.copy(
                captainId = playerId,
                viceCaptainId = if (it.viceCaptainId == playerId) null else it.viceCaptainId
            )
        }
        showToast("Captain selected (2x Points multiplier)")
    }

    fun setFantasyViceCaptain(playerId: String) {
        if (!_userFantasyTeam.value.selectedPlayerIds.contains(playerId)) {
            showToast("Add player to your 11 first!")
            return
        }
        _userFantasyTeam.update {
            it.copy(
                viceCaptainId = playerId,
                captainId = if (it.captainId == playerId) null else it.captainId
            )
        }
        showToast("Vice-Captain selected (1.5x Points multiplier)")
    }

    fun saveFantasyTeam(teamName: String) {
        val count = _userFantasyTeam.value.selectedPlayerIds.size
        if (count != 11) {
            showToast("Please select exactly 11 players! (Selected: $count/11)")
            return
        }
        if (_userFantasyTeam.value.captainId == null) {
            showToast("Please select a Captain (2x points)!")
            return
        }
        val cleanName = teamName.ifBlank { "My Grassroots XI" }
        _userFantasyTeam.update { it.copy(teamName = cleanName) }
        showToast("Virtual Fantasy 11 saved! 100% Free Fan League active.")
    }

    // 4. Player Marketplace / Urgent Requirements (Requirement)
    private val _playerRequirements = MutableStateFlow(SampleData.samplePlayerRequirements)
    val playerRequirements: StateFlow<List<PlayerRequirement>> = _playerRequirements.asStateFlow()
    val isPostRequirementDialogOpen = MutableStateFlow(false)

    fun postPlayerRequirement(
        team: String,
        role: String,
        date: String,
        ground: String,
        phone: String,
        overs: String
    ) {
        val newReq = PlayerRequirement(
            id = "req_${System.currentTimeMillis()}",
            teamName = team.ifBlank { "My Team" },
            roleNeeded = role.ifBlank { "Fast Bowler" },
            matchDate = date.ifBlank { "Upcoming Match" },
            groundCity = ground.ifBlank { "Local Cricket Ground" },
            oversType = overs.ifBlank { "20 Overs Match" },
            contactPhone = phone.ifBlank { _userProfile.value.phone },
            urgent = true,
            postedBy = "Capt. ${_userProfile.value.name}",
            applicantsCount = 0,
            isApplied = false,
            timeAgo = "Just now"
        )
        _playerRequirements.update { listOf(newReq) + it }
        isPostRequirementDialogOpen.value = false
        showToast("Player requirement posted! Available players can now request to join.")
    }

    fun applyForRequirement(reqId: String) {
        _playerRequirements.update { list ->
            list.map { req ->
                if (req.id == reqId) {
                    if (req.isApplied) req else req.copy(
                        isApplied = true,
                        applicantsCount = req.applicantsCount + 1
                    )
                } else req
            }
        }
        showToast("Request to join sent to Captain! They will call you on your mobile.")
    }

    // 5. Auto-Text Commentary (Requirement)
    private val _commentaryList = MutableStateFlow(SampleData.sampleCommentaryList)
    val commentaryList: StateFlow<List<CommentaryItem>> = _commentaryList.asStateFlow()
    val commentaryLanguage = MutableStateFlow("Gujarati") // "Gujarati", "Hindi", "English"

    fun setCommentaryLanguage(lang: String) {
        commentaryLanguage.value = lang
    }

    private data class CommentaryTexts(
        val headline: String,
        val guj: String,
        val hin: String,
        val eng: String
    )

    fun addCommentaryEvent(
        runs: Int,
        isWicket: Boolean,
        extraType: ExtraType? = null,
        bowler: Player,
        striker: Player,
        overText: String
    ) {
        val texts = when {
            isWicket -> CommentaryTexts(
                headline = "WICKET! Massive breakthrough!",
                guj = "આઉટ! મોટી સફળતા! બોલરે સ્ટમ્પ ઉડાવીને મોટી વિકેટ ઝડપી લીધી!",
                hin = "विकेट! बड़ा झटका! गेंदबाज ने जबरदस्त गेंद पर बल्लेबाज को चलता किया!",
                eng = "OUT! Massive breakthrough! The fielding side erupts in celebration!"
            )
            extraType == ExtraType.WIDE -> CommentaryTexts(
                headline = "Wide Ball",
                guj = "વાઈડ બોલ! લેગ સ્ટમ્પ બહાર દિશાહીન બોલ, વધારાનો એક રન મળ્યો.",
                hin = "वाइड गेंद! लेग स्टंप के काफी बाहर, एक अतिरिक्त रन मिला.",
                eng = "Wide ball! Strays down the leg side, bonus run awarded."
            )
            extraType == ExtraType.NO_BALL -> CommentaryTexts(
                headline = "NO BALL! Free Hit!",
                guj = "નો બોલ! બોલરનો પગ લાઈન બહાર અને હવે પછીના બોલ પર ફ્રી હિટ મળશે!",
                hin = "नो बॉल! ओवरस्टेपिंग की वजह से नो बॉल, अब अगली गेंद पर फ्री हिट!",
                eng = "NO BALL! Overstepped the bowling crease, free hit upcoming!"
            )
            runs == 6 -> CommentaryTexts(
                headline = "MAXIMUM! 6 Runs!",
                guj = "હવામાં શાનદાર શોટ અને આ સીધા 6 રન! બોલ સીધો દર્શકોની વચ્ચે!",
                hin = "हवा में खूबसूरत शॉट और गेंद सीधे दर्शकों के बीच 6 रन के लिए!",
                eng = "What a magnificent towering shot sailing into the stands for a huge SIX!"
            )
            runs == 4 -> CommentaryTexts(
                headline = "FOUR! Crisp Boundary!",
                guj = "ચાર રન! ગેપમાં સુંદર કટ શોટ અને બોલ સીધો બાઉન્ડ્રી લાઈન બહાર!",
                hin = "चार रन! खूबसूरत टाइमिंग के साथ गेंद गोली की रफ्तार से बाउंड्री पार!",
                eng = "CRACKING FOUR! Pierces the cover field with sublime timing to the fence!"
            )
            runs == 2 -> CommentaryTexts(
                headline = "Two Runs",
                guj = "ડીપ મિડવિકેટ તરફ ડ્રાઈવ કરીને ઉત્તમ રનિંગ સાથે બે રન લીધા.",
                hin = "गैप में खेलकर दोनों बल्लेबाजों ने दौड़कर दो रन बटोरे.",
                eng = "Driven into the deep gap, excellent running between the wickets for two."
            )
            runs == 1 -> CommentaryTexts(
                headline = "Single Taken",
                guj = "હળવા હાથે ટેપ કરીને ઝડપી સિંગલ રન પૂર્ણ કર્યો.",
                hin = "हल्के हाथों से टैप किया और तेजी से एक रन पूरा किया.",
                eng = "Pushed gently into the covers for a quick, smart single."
            )
            else -> CommentaryTexts(
                headline = "Dot Ball",
                guj = "સારો બોલ! બેટ્સમેન પૂરી રીતે બીટ થયો, કોઈ રન નહીં.",
                hin = "शानदार गेंद! बल्लेबाज पूरी तरह बीट हुए, कोई रन नहीं.",
                eng = "Dot ball! Crisp length on off-stump, solidly defended."
            )
        }

        val newItem = CommentaryItem(
            id = "c_${System.currentTimeMillis()}",
            overText = overText,
            headline = texts.headline,
            commentaryGujarati = "${striker.name} ને ${bowler.name}: ${texts.guj}",
            commentaryHindi = "${striker.name} को ${bowler.name}: ${texts.hin}",
            commentaryEnglish = "${bowler.name} to ${striker.name}: ${texts.eng}",
            bowlerName = bowler.name,
            batsmanName = striker.name,
            runs = runs,
            isWicket = isWicket,
            isBoundary = (runs == 4),
            isSix = (runs == 6),
            timestamp = "Live"
        )
        _commentaryList.update { listOf(newItem) + it.take(40) }
    }

    // Selected Player for full profile & record viewing dialog
    private val _selectedViewingPlayer = MutableStateFlow<Player?>(null)
    val selectedViewingPlayer: StateFlow<Player?> = _selectedViewingPlayer.asStateFlow()

    fun viewPlayer(player: Player) {
        _selectedViewingPlayer.value = player
    }

    fun closePlayerView() {
        _selectedViewingPlayer.value = null
    }

    // Instant Live Data Refresh
    val isRefreshing = MutableStateFlow(false)
    fun refreshData() {
        viewModelScope.launch {
            isRefreshing.value = true
            delay(400)
            _currentMatch.update { it?.copy() }
            _matchesList.update { it.toList() }
            isRefreshing.value = false
        }
    }

    // Notification Preferences
    private val _notificationSettings = MutableStateFlow(NotificationPreferences())
    val notificationSettings: StateFlow<NotificationPreferences> = _notificationSettings.asStateFlow()

    fun updateNotificationSettings(
        liveScore: Boolean,
        boundary: Boolean,
        wicket: Boolean,
        soundVibration: Boolean,
        reminder: Boolean
    ) {
        _notificationSettings.value = NotificationPreferences(
            liveScoreAlerts = liveScore,
            boundaryAlerts = boundary,
            wicketAlerts = wicket,
            soundAndVibration = soundVibration,
            matchReminders = reminder
        )
        showToast("Notification settings saved successfully!")
    }

    // App Language - Default English as requested
    val selectedLanguage = MutableStateFlow("English")
    fun setLanguage(lang: String) {
        selectedLanguage.value = lang
        showToast("Language changed to $lang")
    }

    // User Login / Authentication state
    val isLoggedIn = MutableStateFlow(true)

    // Feedback Toast / Banner message
    val toastMessage = MutableStateFlow<String?>(null)
    fun showToast(message: String) {
        toastMessage.value = message
    }
    fun clearToast() {
        toastMessage.value = null
    }

    // Mobile Number Registration & Settings Dialog Triggers
    val isRegisterDialogOpen = MutableStateFlow(false)
    val isNotificationDialogOpen = MutableStateFlow(false)
    val isLanguageDialogOpen = MutableStateFlow(false)
    val isDeleteAccountDialogOpen = MutableStateFlow(false)
    val isClearDataDialogOpen = MutableStateFlow(false)
    val isLogoutDialogOpen = MutableStateFlow(false)
    val isSupportDialogOpen = MutableStateFlow(false)
    val isAboutDialogOpen = MutableStateFlow(false)
    val isPrivacyPolicyDialogOpen = MutableStateFlow(false)

    // Register with Mobile Number
    fun registerWithMobile(
        name: String,
        phone: String,
        city: String = "Naliya",
        role: String = "All-rounder",
        jersey: Int = 7
    ) {
        val cleanName = name.ifBlank { "Player" }
        val cleanPhone = phone.ifBlank { "9876543210" }
        val cleanCity = city.ifBlank { "Naliya" }

        _userProfile.update { current ->
            current.copy(
                name = cleanName,
                phone = cleanPhone,
                city = cleanCity,
                title = "$role & Player",
                jerseyNumber = jersey
            )
        }

        savePlayerToRoster(
            Player(
                id = _userProfile.value.id,
                name = cleanName,
                phone = cleanPhone,
                city = cleanCity,
                role = role,
                jerseyNumber = jersey,
                matches = _userProfile.value.matches,
                runs = _userProfile.value.runs,
                wickets = _userProfile.value.wickets
            )
        )

        isLoggedIn.value = true
        isRegisterDialogOpen.value = false
        showToast("Registered successfully with +91 $cleanPhone!")
    }

    // Clear Cache and Data action
    fun clearAppData() {
        searchQuery.value = ""
        activeFilterTab.value = "Contacts (315)"
        _currentMatch.update { it?.copy() }
        isClearDataDialogOpen.value = false
        showToast("App cache and temporary data cleared successfully!")
    }

    // Logout action
    fun logout() {
        isLoggedIn.value = false
        isLogoutDialogOpen.value = false
        showToast("Logged out successfully.")
    }

    // Quick Login
    fun login(name: String = "Kasam Jat", phone: String = "8980294520") {
        isLoggedIn.value = true
        _userProfile.update { it.copy(name = name, phone = phone) }
        showToast("Logged in as $name (+91 $phone)")
    }

    // Delete Account action
    fun deleteAccount() {
        _userProfile.value = UserProfile(
            id = "guest_${System.currentTimeMillis()}",
            name = "Guest User",
            title = "New Player",
            phone = "",
            city = "Naliya",
            matches = 0,
            runs = 0,
            wickets = 0,
            catches = 0,
            runOuts = 0,
            stumpings = 0,
            highestScore = "-",
            bestBowling = "-",
            fifties = 0,
            hundreds = 0,
            fours = 0,
            sixes = 0,
            teamsRepresented = emptyList(),
            matchHistory = emptyList()
        )
        _currentMatch.value = null
        _matchesList.value = emptyList()
        isLoggedIn.value = false
        isDeleteAccountDialogOpen.value = false
        _screenStack.value = listOf(AppScreen.Home)
        showToast("Your account and data have been deleted successfully.")
    }

    // Spectator vs Scorer Mode
    // When true: Read-only live spectator mode (no score editing keypad, live updates from scorer)
    // When false: Scorer mode (full keypad, out dialog, bowler changes, extras)
    private val _isSpectatorMode = MutableStateFlow(true)
    val isSpectatorMode: StateFlow<Boolean> = _isSpectatorMode.asStateFlow()

    // Navigation Stack - Starts with Splash Screen showing the Quick Heroes 11 logo
    private val _screenStack = MutableStateFlow<List<AppScreen>>(listOf(AppScreen.Splash))
    val screenStack: StateFlow<List<AppScreen>> = _screenStack.asStateFlow()

    val currentScreen: AppScreen
        get() = _screenStack.value.lastOrNull() ?: AppScreen.Home

    // Active Live Match (Clean state - starts null, no dummy matches!)
    private val _currentMatch = MutableStateFlow<CricketMatch?>(null)
    val currentMatch: StateFlow<CricketMatch?> = _currentMatch.asStateFlow()

    // Matches List (Clean initial list without dummy matches)
    private val _matchesList = MutableStateFlow<List<CricketMatch>>(emptyList())
    val matchesList: StateFlow<List<CricketMatch>> = _matchesList.asStateFlow()

    // Clean players list with mobile numbers, fielding stats, and match histories
    private val _players = MutableStateFlow<List<Player>>(
        listOf(
            Player(
                id = "p_u1",
                name = "Kasam Jat",
                city = "Naliya",
                phone = "8980294520",
                matches = 34,
                runs = 1240,
                wickets = 48,
                catches = 22,
                runOuts = 7,
                stumpings = 0,
                fifties = 8,
                hundreds = 2,
                highestScore = "88*",
                bestBowling = "4/16",
                strikeRate = 145.2f,
                battingAvg = 44.3f,
                bowlingEconomy = 6.4f,
                role = "All-rounder",
                battingStyle = "Right-hand bat",
                bowlingStyle = "Right-arm medium",
                jerseyNumber = 7,
                teamsRepresented = listOf("Naliya Royals", "Kutch XI", "Naliya Super Kings"),
                matchHistory = listOf(
                    PlayerMatchRecord(
                        matchId = "m1", matchTitle = "Naliya Royals vs Kutch Warriors", date = "03 Nov 2026",
                        teamName = "Naliya Royals", opponentName = "Kutch Warriors",
                        runs = 46, balls = 24, fours = 5, sixes = 2, wickets = 3, runsConceded = 18, oversBowled = "2.0", catches = 2, result = "Won by 24 runs"
                    ),
                    PlayerMatchRecord(
                        matchId = "m2", matchTitle = "Naliya Royals vs Bhuj Strikers", date = "28 Oct 2026",
                        teamName = "Naliya Royals", opponentName = "Bhuj Strikers",
                        runs = 58, balls = 31, fours = 6, sixes = 3, wickets = 2, runsConceded = 22, oversBowled = "3.0", catches = 1, result = "Won by 6 wkts"
                    ),
                    PlayerMatchRecord(
                        matchId = "m3", matchTitle = "Kutch XI vs Gandhidham Lions", date = "15 Oct 2026",
                        teamName = "Kutch XI", opponentName = "Gandhidham Lions",
                        runs = 34, balls = 19, fours = 4, sixes = 1, wickets = 1, runsConceded = 25, oversBowled = "2.0", catches = 0, result = "Lost by 12 runs"
                    )
                )
            ),
            Player(
                id = "p_u2",
                name = "Abuahemad Jat",
                city = "Naliya",
                phone = "9825101122",
                matches = 976,
                runs = 19336,
                wickets = 914,
                catches = 340,
                runOuts = 42,
                fifties = 88,
                hundreds = 24,
                highestScore = "142*",
                bestBowling = "5/12",
                strikeRate = 152.4f,
                battingAvg = 48.6f,
                bowlingEconomy = 6.1f,
                role = "Batsman",
                battingStyle = "Right-hand bat",
                bowlingStyle = "Right-arm off break",
                jerseyNumber = 18,
                teamsRepresented = listOf("Naliya Royals", "Kutch Warriors"),
                matchHistory = listOf(
                    PlayerMatchRecord(
                        matchId = "m1", matchTitle = "Naliya Royals vs Kutch Warriors", date = "03 Nov 2026",
                        teamName = "Naliya Royals", opponentName = "Kutch Warriors",
                        runs = 38, balls = 22, fours = 4, sixes = 2, wickets = 0, runsConceded = 0, oversBowled = "0.0", catches = 1, result = "Won by 24 runs"
                    )
                )
            ),
            Player(
                id = "p_u3",
                name = "Hasan.a.Jat",
                city = "Naliya",
                phone = "9723412345",
                matches = 829,
                runs = 13276,
                wickets = 599,
                catches = 280,
                runOuts = 31,
                fifties = 62,
                hundreds = 14,
                highestScore = "118*",
                bestBowling = "6/22",
                strikeRate = 141.0f,
                battingAvg = 42.1f,
                bowlingEconomy = 6.5f,
                role = "All-rounder",
                battingStyle = "Left-hand bat",
                bowlingStyle = "Slow left-arm orthodox",
                jerseyNumber = 7,
                teamsRepresented = listOf("Naliya Royals", "Bhuj XI"),
                matchHistory = listOf(
                    PlayerMatchRecord(
                        matchId = "m1", matchTitle = "Naliya Royals vs Kutch Warriors", date = "03 Nov 2026",
                        teamName = "Naliya Royals", opponentName = "Kutch Warriors",
                        runs = 27, balls = 16, fours = 3, sixes = 1, wickets = 2, runsConceded = 19, oversBowled = "2.0", catches = 1, result = "Won by 24 runs"
                    )
                )
            ),
            Player(
                id = "p_u4",
                name = "Latif Jat (L-J)",
                city = "Naliya",
                phone = "9879054321",
                matches = 794,
                runs = 8825,
                wickets = 616,
                catches = 195,
                runOuts = 22,
                fifties = 28,
                hundreds = 4,
                highestScore = "84",
                bestBowling = "5/14",
                strikeRate = 128.5f,
                battingAvg = 26.4f,
                bowlingEconomy = 5.8f,
                role = "Bowler",
                battingStyle = "Right-hand bat",
                bowlingStyle = "Right-arm fast",
                jerseyNumber = 99,
                teamsRepresented = listOf("Naliya Royals", "Naliya Tigers"),
                matchHistory = listOf(
                    PlayerMatchRecord(
                        matchId = "m1", matchTitle = "Naliya Royals vs Kutch Warriors", date = "03 Nov 2026",
                        teamName = "Naliya Royals", opponentName = "Kutch Warriors",
                        runs = 12, balls = 6, fours = 1, sixes = 1, wickets = 3, runsConceded = 15, oversBowled = "2.0", catches = 0, result = "Won by 24 runs"
                    )
                )
            ),
            Player(
                id = "p_u5",
                name = "M s jat",
                city = "Naliya",
                phone = "9909988776",
                matches = 758,
                runs = 7108,
                wickets = 480,
                catches = 410,
                runOuts = 55,
                stumpings = 82,
                fifties = 32,
                hundreds = 5,
                highestScore = "96*",
                bestBowling = "4/20",
                strikeRate = 134.8f,
                battingAvg = 31.2f,
                bowlingEconomy = 6.9f,
                role = "Wk-Batsman",
                battingStyle = "Right-hand bat",
                bowlingStyle = "Right-arm medium",
                jerseyNumber = 45,
                teamsRepresented = listOf("Naliya Royals", "Kutch Warriors"),
                matchHistory = listOf(
                    PlayerMatchRecord(
                        matchId = "m1", matchTitle = "Naliya Royals vs Kutch Warriors", date = "03 Nov 2026",
                        teamName = "Naliya Royals", opponentName = "Kutch Warriors",
                        runs = 18, balls = 12, fours = 2, sixes = 0, wickets = 0, runsConceded = 0, oversBowled = "0.0", catches = 2, result = "Won by 24 runs"
                    )
                )
            )
        )
    )
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    // Auto-save player into persistent roster (Item 13)
    fun savePlayerToRoster(player: Player) {
        _players.update { list ->
            val existingIndex = list.indexOfFirst {
                (it.phone.isNotBlank() && it.phone == player.phone) ||
                it.name.trim().equals(player.name.trim(), ignoreCase = true)
            }
            if (existingIndex >= 0) {
                val current = list[existingIndex]
                val mergedTeams = (current.teamsRepresented + player.teamsRepresented).distinct()
                val mergedHistory = if (player.matchHistory.isNotEmpty()) {
                    (player.matchHistory + current.matchHistory).distinctBy { it.matchId.ifBlank { "${it.matchTitle}_${it.date}" } }
                } else current.matchHistory

                val updated = current.copy(
                    phone = if (player.phone.isNotBlank()) player.phone else current.phone,
                    city = if (player.city.isNotBlank()) player.city else current.city,
                    role = player.role,
                    runs = maxOf(current.runs, player.runs),
                    wickets = maxOf(current.wickets, player.wickets),
                    matches = maxOf(current.matches, player.matches),
                    catches = maxOf(current.catches, player.catches),
                    teamsRepresented = mergedTeams,
                    matchHistory = mergedHistory
                )
                list.toMutableList().apply { set(existingIndex, updated) }
            } else {
                list + player
            }
        }
    }

    // Leaderboards for Community & Player Profile (Item 12)
    fun getBattingLeaderboard(currentPlayerName: String = _userProfile.value.name): List<com.example.data.LeaderboardItem> {
        val sorted = _players.value.sortedByDescending { it.runs }
        return sorted.mapIndexed { idx, p ->
            com.example.data.LeaderboardItem(
                rank = idx + 1,
                playerName = p.name,
                teamOrCity = if (p.city.isNotBlank()) p.city else "Naliya",
                statValue = "${p.runs} Runs",
                statLabel = "${p.matches} Matches • Avg ${if (p.battingAvg > 0) String.format("%.1f", p.battingAvg) else "38.5"} • SR ${if (p.strikeRate > 0) String.format("%.1f", p.strikeRate) else "142.0"}",
                phone = p.phone,
                isCurrentUser = p.name.equals(currentPlayerName, ignoreCase = true)
            )
        }
    }

    fun getBowlingLeaderboard(currentPlayerName: String = _userProfile.value.name): List<com.example.data.LeaderboardItem> {
        val sorted = _players.value.sortedByDescending { it.wickets }
        return sorted.mapIndexed { idx, p ->
            com.example.data.LeaderboardItem(
                rank = idx + 1,
                playerName = p.name,
                teamOrCity = if (p.city.isNotBlank()) p.city else "Naliya",
                statValue = "${p.wickets} Wkts",
                statLabel = "${p.matches} Matches • Best ${p.bestBowling.ifBlank { "4/16" }} • Eco ${if (p.bowlingEconomy > 0) String.format("%.1f", p.bowlingEconomy) else "6.4"}",
                phone = p.phone,
                isCurrentUser = p.name.equals(currentPlayerName, ignoreCase = true)
            )
        }
    }

    fun getFieldingLeaderboard(currentPlayerName: String = _userProfile.value.name): List<com.example.data.LeaderboardItem> {
        val sorted = _players.value.sortedByDescending { it.catches + it.runOuts }
        return sorted.mapIndexed { idx, p ->
            com.example.data.LeaderboardItem(
                rank = idx + 1,
                playerName = p.name,
                teamOrCity = if (p.city.isNotBlank()) p.city else "Naliya",
                statValue = "${p.catches} Catches",
                statLabel = "${p.runOuts} Run-Outs • ${p.stumpings} Stumpings • ${p.matches} Matches",
                phone = p.phone,
                isCurrentUser = p.name.equals(currentPlayerName, ignoreCase = true)
            )
        }
    }

    fun getPlayerRank(playerName: String, category: String = "Batting"): Pair<Int, String> {
        val list = when (category) {
            "Bowling" -> getBowlingLeaderboard(playerName)
            "Fielding" -> getFieldingLeaderboard(playerName)
            else -> getBattingLeaderboard(playerName)
        }
        val item = list.find { it.playerName.equals(playerName, ignoreCase = true) }
        val rank = item?.rank ?: 1
        val title = when (rank) {
            1 -> "🥇 Rank #1 (Champion)"
            2 -> "🥈 Rank #2 (Elite)"
            3 -> "🥉 Rank #3 (Star)"
            in 4..10 -> "⭐ Top 10"
            else -> "Rank #$rank"
        }
        return Pair(rank, title)
    }

    // UI Dialog & Sheet states
    val isPlusMenuOpen = MutableStateFlow(false)
    val isHelpModalOpen = MutableStateFlow(false)
    val isWicketModalOpen = MutableStateFlow(false)
    val isBowlerModalOpen = MutableStateFlow(false)
    val isOverCompleteModalOpen = MutableStateFlow(false)
    val isMatchFinishedModalOpen = MutableStateFlow(false)
    val searchQuery = MutableStateFlow("")
    val activeFilterTab = MutableStateFlow("Contacts (315)")

    fun finishSplash() {
        _screenStack.value = listOf(AppScreen.Home)
    }

    // Navigation methods
    fun navigateTo(screen: AppScreen) {
        isPlusMenuOpen.value = false
        _screenStack.update { current ->
            if (current.lastOrNull() == screen) current else current + screen
        }
    }

    fun navigateBack(): Boolean {
        if (_screenStack.value.size > 1) {
            _screenStack.update { it.dropLast(1) }
            return true
        }
        return false
    }

    // Role switcher: Switch between Live Spectator Mode and Match Scorer Mode
    fun setSpectatorMode(isSpectator: Boolean) {
        _isSpectatorMode.value = isSpectator
    }

    // Open any match for viewing as a Spectator (No scoring keypad allowed)
    fun openMatchAsSpectator(match: CricketMatch) {
        _currentMatch.value = match
        _isSpectatorMode.value = true
        navigateTo(AppScreen.LiveScoring)
    }

    // Open match as Scorer (Keypad enabled for authorized scorer)
    fun openMatchAsScorer(match: CricketMatch) {
        _currentMatch.value = match
        _isSpectatorMode.value = false
        navigateTo(AppScreen.LiveScoring)
    }

    // User Profile Updates
    fun updateUserProfile(
        name: String,
        title: String,
        phone: String,
        city: String,
        matches: Int,
        runs: Int,
        wickets: Int,
        highestScore: String,
        bestBowling: String
    ) {
        _userProfile.update {
            it.copy(
                name = name.ifBlank { it.name },
                title = title.ifBlank { it.title },
                phone = phone.ifBlank { it.phone },
                city = city.ifBlank { it.city },
                matches = matches.coerceAtLeast(0),
                runs = runs.coerceAtLeast(0),
                wickets = wickets.coerceAtLeast(0),
                highestScore = highestScore.ifBlank { it.highestScore },
                bestBowling = bestBowling.ifBlank { it.bestBowling }
            )
        }
    }

    fun updateProfilePhoto(uri: String?) {
        _userProfile.update {
            it.copy(photoUri = uri)
        }
    }

    fun updateProfileAvatar(presetIndex: Int) {
        _userProfile.update {
            it.copy(avatarPreset = presetIndex, photoUri = null)
        }
    }

    fun toggleFollow(playerId: String) {
        _players.update { list ->
            list.map {
                if (it.id == playerId) it.copy(isFollowing = !it.isFollowing) else it
            }
        }
    }

    // Cricket Scoring Logic: Ball Scored
    fun recordRuns(runs: Int) {
        val match = _currentMatch.value ?: return
        val inning = match.currentInning ?: return
        if (inning.isCompleted) return

        val strikerScore = inning.striker ?: return
        val bowlerScore = inning.activeBowler ?: return

        if (runs == 4) {
            triggerCelebration(CelebrationType.FOUR)
        } else if (runs == 6) {
            triggerCelebration(CelebrationType.SIX)
        }

        val newStrikerScore = strikerScore.copy(
            runs = strikerScore.runs + runs,
            balls = strikerScore.balls + 1,
            fours = if (runs == 4) strikerScore.fours + 1 else strikerScore.fours,
            sixes = if (runs == 6) strikerScore.sixes + 1 else strikerScore.sixes
        )

        val newBowlerScore = bowlerScore.copy(
            legalBalls = bowlerScore.legalBalls + 1,
            runsConceded = bowlerScore.runsConceded + runs
        )

        val newLegalBalls = inning.legalBalls + 1
        val isOverEnd = (newLegalBalls % 6 == 0)

        // Strike changes on odd runs (1, 3, 5) OR at end of over (unless odd runs + over end)
        val shouldSwapForRuns = (runs % 2 != 0)
        var nextStrikerId = inning.currentStrikerId
        var nextNonStrikerId = inning.currentNonStrikerId

        if (shouldSwapForRuns) {
            val temp = nextStrikerId
            nextStrikerId = nextNonStrikerId
            nextNonStrikerId = temp
        }

        if (isOverEnd) {
            val temp = nextStrikerId
            nextStrikerId = nextNonStrikerId
            nextNonStrikerId = temp
        }

        val ballEvent = BallEvent(
            id = "ball_${System.currentTimeMillis()}",
            overNumber = inning.legalBalls / 6,
            ballInOver = (inning.legalBalls % 6) + 1,
            runsScored = runs,
            striker = strikerScore.player,
            nonStriker = inning.nonStriker?.player ?: strikerScore.player,
            bowler = bowlerScore.player,
            displayText = runs.toString()
        )

        val updatedBatsmen = inning.batsmenScores.map {
            if (it.player.id == strikerScore.player.id) newStrikerScore else it
        }

        val updatedBowlers = inning.bowlerScores.map {
            if (it.player.id == bowlerScore.player.id) newBowlerScore else it
        }

        val newTotalRuns = inning.totalRuns + runs
        val isTargetReached = (match.currentInningIndex == 1 && match.target != null && newTotalRuns >= match.target!!)
        val isAllOversCompleted = (newLegalBalls >= match.totalOvers * 6)

        val isInningDone = isTargetReached || isAllOversCompleted || (inning.wickets >= 10)

        val updatedInning = inning.copy(
            totalRuns = newTotalRuns,
            legalBalls = newLegalBalls,
            batsmenScores = updatedBatsmen,
            bowlerScores = updatedBowlers,
            currentStrikerId = nextStrikerId,
            currentNonStrikerId = nextNonStrikerId,
            ballHistory = inning.ballHistory + ballEvent,
            isCompleted = isInningDone
        )

        // Queue offline event if in offline mode
        if (isOfflineMode.value) {
            _pendingOfflineEvents.update {
                it + OfflineScoringEvent(
                    id = "off_${System.currentTimeMillis()}",
                    matchId = match.id,
                    type = "RUNS",
                    description = "$runs runs scored by ${strikerScore.player.name}"
                )
            }
        }

        // Add auto-generated commentary
        addCommentaryEvent(
            runs = runs,
            isWicket = false,
            extraType = null,
            bowler = bowlerScore.player,
            striker = strikerScore.player,
            overText = "${inning.legalBalls / 6}.${(inning.legalBalls % 6) + 1}"
        )

        // Check and award digital badges
        if (newStrikerScore.runs >= 50 && strikerScore.runs < 50) {
            awardBadge("b_fifty")
        }
        if (newStrikerScore.runs >= 100 && strikerScore.runs < 100) {
            awardBadge("b_century")
        }
        if (newStrikerScore.sixes >= 5) {
            awardBadge("b_sixer")
        }

        updateInningAndCheckMatchEnd(updatedInning, isOverEnd, isInningDone, isTargetReached)
    }

    // Cricket Scoring Logic: Extras (Wide, No Ball, Bye, Leg Bye)
    fun recordExtra(extraType: ExtraType, extraRuns: Int = 1, runsOffBat: Int = 0) {
        val match = _currentMatch.value ?: return
        val inning = match.currentInning ?: return
        if (inning.isCompleted) return

        val strikerScore = inning.striker ?: return
        val bowlerScore = inning.activeBowler ?: return

        val isLegal = (extraType == ExtraType.BYE || extraType == ExtraType.LEG_BYE || extraType == ExtraType.PENALTY)
        val bowlerConceded = when (extraType) {
            ExtraType.WIDE, ExtraType.NO_BALL -> extraRuns + runsOffBat
            else -> 0
        }

        val newStrikerScore = if (extraType == ExtraType.NO_BALL && runsOffBat > 0) {
            strikerScore.copy(
                runs = strikerScore.runs + runsOffBat,
                balls = strikerScore.balls + 1,
                fours = if (runsOffBat == 4) strikerScore.fours + 1 else strikerScore.fours,
                sixes = if (runsOffBat == 6) strikerScore.sixes + 1 else strikerScore.sixes
            )
        } else if (isLegal) {
            strikerScore.copy(balls = strikerScore.balls + 1)
        } else {
            strikerScore
        }

        val newBowlerScore = bowlerScore.copy(
            legalBalls = if (isLegal) bowlerScore.legalBalls + 1 else bowlerScore.legalBalls,
            runsConceded = bowlerScore.runsConceded + bowlerConceded
        )

        val newLegalBalls = if (isLegal) inning.legalBalls + 1 else inning.legalBalls
        val isOverEnd = isLegal && (newLegalBalls % 6 == 0)

        val totalExtraRunsAdded = extraRuns + runsOffBat
        val newTotalRuns = inning.totalRuns + totalExtraRunsAdded

        val newExtras = when (extraType) {
            ExtraType.WIDE -> inning.extras.copy(wides = inning.extras.wides + totalExtraRunsAdded)
            ExtraType.NO_BALL -> inning.extras.copy(noBalls = inning.extras.noBalls + extraRuns)
            ExtraType.BYE -> inning.extras.copy(byes = inning.extras.byes + extraRuns)
            ExtraType.LEG_BYE -> inning.extras.copy(legByes = inning.extras.legByes + extraRuns)
            ExtraType.PENALTY -> inning.extras.copy(penalty = inning.extras.penalty + extraRuns)
        }

        var nextStrikerId = inning.currentStrikerId
        var nextNonStrikerId = inning.currentNonStrikerId

        val totalRunsForSwap = if (extraType == ExtraType.NO_BALL) runsOffBat else (extraRuns - 1)
        if (totalRunsForSwap % 2 != 0) {
            val temp = nextStrikerId
            nextStrikerId = nextNonStrikerId
            nextNonStrikerId = temp
        }

        if (isOverEnd) {
            val temp = nextStrikerId
            nextStrikerId = nextNonStrikerId
            nextNonStrikerId = temp
        }

        val displayLabel = when (extraType) {
            ExtraType.WIDE -> if (totalExtraRunsAdded > 1) "${totalExtraRunsAdded}Wd" else "Wd"
            ExtraType.NO_BALL -> if (runsOffBat > 0) "${runsOffBat + 1}Nb" else "Nb"
            ExtraType.BYE -> "${extraRuns}B"
            ExtraType.LEG_BYE -> "${extraRuns}Lb"
            ExtraType.PENALTY -> "${extraRuns}Pen"
        }

        val ballEvent = BallEvent(
            id = "ball_${System.currentTimeMillis()}",
            overNumber = inning.legalBalls / 6,
            ballInOver = (inning.legalBalls % 6) + if (isLegal) 1 else 0,
            runsScored = runsOffBat,
            extraRuns = extraRuns,
            extraType = extraType,
            isLegalBall = isLegal,
            striker = strikerScore.player,
            nonStriker = inning.nonStriker?.player ?: strikerScore.player,
            bowler = bowlerScore.player,
            displayText = displayLabel
        )

        val updatedBatsmen = inning.batsmenScores.map {
            if (it.player.id == strikerScore.player.id) newStrikerScore else it
        }

        val updatedBowlers = inning.bowlerScores.map {
            if (it.player.id == bowlerScore.player.id) newBowlerScore else it
        }

        val isTargetReached = (match.currentInningIndex == 1 && match.target != null && newTotalRuns >= match.target!!)
        val isAllOversCompleted = isLegal && (newLegalBalls >= match.totalOvers * 6)
        val isInningDone = isTargetReached || isAllOversCompleted

        val updatedInning = inning.copy(
            totalRuns = newTotalRuns,
            legalBalls = newLegalBalls,
            extras = newExtras,
            batsmenScores = updatedBatsmen,
            bowlerScores = updatedBowlers,
            currentStrikerId = nextStrikerId,
            currentNonStrikerId = nextNonStrikerId,
            ballHistory = inning.ballHistory + ballEvent,
            isCompleted = isInningDone
        )

        // Queue offline event if in offline mode
        if (isOfflineMode.value) {
            _pendingOfflineEvents.update {
                it + OfflineScoringEvent(
                    id = "off_${System.currentTimeMillis()}",
                    matchId = match.id,
                    type = "EXTRA",
                    description = "$displayLabel bowled by ${bowlerScore.player.name}"
                )
            }
        }

        // Add auto commentary
        addCommentaryEvent(
            runs = runsOffBat,
            isWicket = false,
            extraType = extraType,
            bowler = bowlerScore.player,
            striker = strikerScore.player,
            overText = "${inning.legalBalls / 6}.${(inning.legalBalls % 6) + if (isLegal) 1 else 0}"
        )

        updateInningAndCheckMatchEnd(updatedInning, isOverEnd, isInningDone, isTargetReached)
    }

    // Cricket Scoring Logic: Wicket
    fun recordWicket(
        dismissalType: DismissalType,
        dismissedPlayerId: String,
        newBatsman: Player,
        fielderName: String? = null
    ) {
        val match = _currentMatch.value ?: return
        val inning = match.currentInning ?: return
        if (inning.isCompleted) return

        val strikerScore = inning.striker ?: return
        val bowlerScore = inning.activeBowler ?: return

        // Auto-save new batsman into persistent roster (Item 13)
        savePlayerToRoster(newBatsman)

        triggerCelebration(CelebrationType.WICKET)

        val isBowlerWicket = (dismissalType != DismissalType.RUN_OUT && dismissalType != DismissalType.RETIRED_HURT)

        val newBowlerScore = bowlerScore.copy(
            legalBalls = bowlerScore.legalBalls + 1,
            wickets = if (isBowlerWicket) bowlerScore.wickets + 1 else bowlerScore.wickets
        )

        val dismissedScore = inning.batsmenScores.find { it.player.id == dismissedPlayerId } ?: strikerScore
        val dismissalText = when (dismissalType) {
            DismissalType.BOWLED -> "b ${bowlerScore.player.name}"
            DismissalType.CAUGHT -> "c ${fielderName ?: "Fielder"} b ${bowlerScore.player.name}"
            DismissalType.LBW -> "lbw b ${bowlerScore.player.name}"
            DismissalType.STUMPED -> "st ${fielderName ?: "Wicketkeeper"} b ${bowlerScore.player.name}"
            DismissalType.RUN_OUT -> "run out (${fielderName ?: "Fielder"})"
            DismissalType.HIT_WICKET -> "hit wicket b ${bowlerScore.player.name}"
            DismissalType.RETIRED_HURT -> "retired hurt"
        }

        val updatedDismissedScore = dismissedScore.copy(
            balls = dismissedScore.balls + 1,
            isOut = true,
            dismissalText = dismissalText
        )

        val newBatsmanScore = BatsmanScore(
            player = newBatsman,
            runs = 0,
            balls = 0,
            isOnStrike = (dismissedPlayerId == inning.currentStrikerId)
        )

        val newLegalBalls = inning.legalBalls + 1
        val isOverEnd = (newLegalBalls % 6 == 0)

        var nextStrikerId = if (dismissedPlayerId == inning.currentStrikerId) newBatsman.id else inning.currentStrikerId
        var nextNonStrikerId = if (dismissedPlayerId == inning.currentNonStrikerId) newBatsman.id else inning.currentNonStrikerId

        if (isOverEnd) {
            val temp = nextStrikerId
            nextStrikerId = nextNonStrikerId
            nextNonStrikerId = temp
        }

        val ballEvent = BallEvent(
            id = "ball_${System.currentTimeMillis()}",
            overNumber = inning.legalBalls / 6,
            ballInOver = (inning.legalBalls % 6) + 1,
            runsScored = 0,
            isWicket = true,
            dismissalType = dismissalType,
            dismissedPlayer = dismissedScore.player,
            fielderName = fielderName,
            striker = strikerScore.player,
            nonStriker = inning.nonStriker?.player ?: strikerScore.player,
            bowler = bowlerScore.player,
            displayText = "W"
        )

        val updatedBatsmen = inning.batsmenScores.map {
            if (it.player.id == dismissedPlayerId) updatedDismissedScore else it
        } + newBatsmanScore

        val updatedBowlers = inning.bowlerScores.map {
            if (it.player.id == bowlerScore.player.id) newBowlerScore else it
        }

        val newWicketsCount = inning.wickets + 1
        val isAllOut = (newWicketsCount >= 10)
        val isAllOversCompleted = (newLegalBalls >= match.totalOvers * 6)
        val isInningDone = isAllOut || isAllOversCompleted

        val updatedInning = inning.copy(
            wickets = newWicketsCount,
            legalBalls = newLegalBalls,
            batsmenScores = updatedBatsmen,
            bowlerScores = updatedBowlers,
            currentStrikerId = nextStrikerId,
            currentNonStrikerId = nextNonStrikerId,
            ballHistory = inning.ballHistory + ballEvent,
            isCompleted = isInningDone
        )

        // Queue offline event if in offline mode
        if (isOfflineMode.value) {
            _pendingOfflineEvents.update {
                it + OfflineScoringEvent(
                    id = "off_${System.currentTimeMillis()}",
                    matchId = match.id,
                    type = "WICKET",
                    description = "Wicket taken by ${bowlerScore.player.name}"
                )
            }
        }

        // Add auto commentary
        addCommentaryEvent(
            runs = 0,
            isWicket = true,
            extraType = null,
            bowler = bowlerScore.player,
            striker = dismissedScore.player,
            overText = "${inning.legalBalls / 6}.${(inning.legalBalls % 6) + 1}"
        )

        // Award Digital Badges
        if (newBowlerScore.wickets >= 3) {
            awardBadge("b_hattrick")
        }
        if (newBowlerScore.wickets >= 5) {
            awardBadge("b_fifer")
        }

        isWicketModalOpen.value = false
        updateInningAndCheckMatchEnd(updatedInning, isOverEnd, isInningDone, false)
    }

    // Cricket Scoring Logic: Undo Last Ball
    fun undoLastBall() {
        val match = _currentMatch.value ?: return
        val inning = match.currentInning ?: return
        if (inning.ballHistory.isEmpty()) return

        val lastBall = inning.ballHistory.last()
        val remainingHistory = inning.ballHistory.dropLast(1)

        // Reverse totals
        val runsToRemove = lastBall.runsScored + lastBall.extraRuns
        val newTotalRuns = (inning.totalRuns - runsToRemove).coerceAtLeast(0)
        val newLegalBalls = if (lastBall.isLegalBall) (inning.legalBalls - 1).coerceAtLeast(0) else inning.legalBalls
        val newWickets = if (lastBall.isWicket) (inning.wickets - 1).coerceAtLeast(0) else inning.wickets

        // Revert extras
        val newExtras = when (lastBall.extraType) {
            ExtraType.WIDE -> inning.extras.copy(wides = (inning.extras.wides - lastBall.extraRuns).coerceAtLeast(0))
            ExtraType.NO_BALL -> inning.extras.copy(noBalls = (inning.extras.noBalls - lastBall.extraRuns).coerceAtLeast(0))
            ExtraType.BYE -> inning.extras.copy(byes = (inning.extras.byes - lastBall.extraRuns).coerceAtLeast(0))
            ExtraType.LEG_BYE -> inning.extras.copy(legByes = (inning.extras.legByes - lastBall.extraRuns).coerceAtLeast(0))
            ExtraType.PENALTY -> inning.extras.copy(penalty = (inning.extras.penalty - lastBall.extraRuns).coerceAtLeast(0))
            null -> inning.extras
        }

        // Revert Striker and Bowler score
        val updatedBatsmen = inning.batsmenScores.map { b ->
            if (b.player.id == lastBall.striker.id) {
                b.copy(
                    runs = (b.runs - lastBall.runsScored).coerceAtLeast(0),
                    balls = if (lastBall.isLegalBall || (lastBall.extraType == ExtraType.NO_BALL && lastBall.runsScored > 0)) (b.balls - 1).coerceAtLeast(0) else b.balls,
                    fours = if (lastBall.runsScored == 4) (b.fours - 1).coerceAtLeast(0) else b.fours,
                    sixes = if (lastBall.runsScored == 6) (b.sixes - 1).coerceAtLeast(0) else b.sixes
                )
            } else if (lastBall.isWicket && b.player.id == lastBall.dismissedPlayer?.id) {
                b.copy(isOut = false, dismissalText = "Not out")
            } else {
                b
            }
        }.filterNot { lastBall.isWicket && it.player.id == inning.currentStrikerId && it.balls == 0 && it.runs == 0 }

        val updatedBowlers = inning.bowlerScores.map { bow ->
            if (bow.player.id == lastBall.bowler.id) {
                val concededToRemove = if (lastBall.extraType == ExtraType.BYE || lastBall.extraType == ExtraType.LEG_BYE) 0 else runsToRemove
                bow.copy(
                    legalBalls = if (lastBall.isLegalBall) (bow.legalBalls - 1).coerceAtLeast(0) else bow.legalBalls,
                    runsConceded = (bow.runsConceded - concededToRemove).coerceAtLeast(0),
                    wickets = if (lastBall.isWicket && lastBall.dismissalType != DismissalType.RUN_OUT) (bow.wickets - 1).coerceAtLeast(0) else bow.wickets
                )
            } else {
                bow
            }
        }

        val updatedInning = inning.copy(
            totalRuns = newTotalRuns,
            legalBalls = newLegalBalls,
            wickets = newWickets,
            extras = newExtras,
            batsmenScores = updatedBatsmen,
            bowlerScores = updatedBowlers,
            currentStrikerId = lastBall.striker.id,
            currentNonStrikerId = lastBall.nonStriker.id,
            currentBowlerId = lastBall.bowler.id,
            ballHistory = remainingHistory,
            isCompleted = false
        )

        val updatedInningsList = match.innings.toMutableList()
        updatedInningsList[match.currentInningIndex] = updatedInning

        _currentMatch.update {
            it?.copy(
                innings = updatedInningsList,
                status = MatchStatus.LIVE,
                resultMessage = null
            )
        }
    }

    fun swapStrike() {
        val match = _currentMatch.value ?: return
        val inning = match.currentInning ?: return
        val currentStr = inning.currentStrikerId
        val currentNonStr = inning.currentNonStrikerId

        val updatedInning = inning.copy(
            currentStrikerId = currentNonStr,
            currentNonStrikerId = currentStr
        )
        val updatedInnings = match.innings.toMutableList()
        updatedInnings[match.currentInningIndex] = updatedInning
        _currentMatch.update { it?.copy(innings = updatedInnings) }
    }

    fun changeBowler(bowler: Player) {
        val match = _currentMatch.value ?: return
        val inning = match.currentInning ?: return

        // Auto-save bowler into persistent roster (Item 13)
        savePlayerToRoster(bowler)

        var bowlersList = inning.bowlerScores
        if (bowlersList.none { it.player.id == bowler.id }) {
            bowlersList = bowlersList + BowlerScore(player = bowler)
        }

        val updatedInning = inning.copy(
            currentBowlerId = bowler.id,
            bowlerScores = bowlersList
        )
        val updatedInnings = match.innings.toMutableList()
        updatedInnings[match.currentInningIndex] = updatedInning
        _currentMatch.update { it?.copy(innings = updatedInnings) }
        isBowlerModalOpen.value = false
        isOverCompleteModalOpen.value = false
    }

    fun startSecondInnings(striker: Player, nonStriker: Player, openingBowler: Player) {
        val match = _currentMatch.value ?: return
        if (match.innings.isEmpty()) return

        val firstInning = match.innings[0]
        val secondInningBattingTeam = firstInning.bowlingTeam
        val secondInningBowlingTeam = firstInning.battingTeam

        val secondInning = Inning(
            id = "inn_2",
            inningNumber = 2,
            battingTeam = secondInningBattingTeam,
            bowlingTeam = secondInningBowlingTeam,
            totalRuns = 0,
            wickets = 0,
            legalBalls = 0,
            batsmenScores = listOf(
                BatsmanScore(player = striker, isOnStrike = true),
                BatsmanScore(player = nonStriker, isOnStrike = false)
            ),
            bowlerScores = listOf(
                BowlerScore(player = openingBowler)
            ),
            currentStrikerId = striker.id,
            currentNonStrikerId = nonStriker.id,
            currentBowlerId = openingBowler.id,
            ballHistory = emptyList(),
            isCompleted = false
        )

        _currentMatch.update {
            it?.copy(
                currentInningIndex = 1,
                innings = listOf(firstInning, secondInning),
                status = MatchStatus.LIVE
            )
        }
    }

    // Create New Match from Match Setup Wizard
    fun createAndStartMatch(
        teamA: Team,
        teamB: Team,
        totalOvers: Int,
        matchType: String,
        ballType: String,
        venue: String,
        tossWinner: Team,
        tossDecision: String,
        striker: Player,
        nonStriker: Player,
        openingBowler: Player
    ) {
        val battingFirst = if (tossDecision == "Bat") tossWinner else (if (tossWinner.id == teamA.id) teamB else teamA)
        val bowlingFirst = if (battingFirst.id == teamA.id) teamB else teamA

        val firstInning = Inning(
            id = "inn_${System.currentTimeMillis()}",
            inningNumber = 1,
            battingTeam = battingFirst,
            bowlingTeam = bowlingFirst,
            totalRuns = 0,
            wickets = 0,
            legalBalls = 0,
            batsmenScores = listOf(
                BatsmanScore(player = striker, isOnStrike = true),
                BatsmanScore(player = nonStriker, isOnStrike = false)
            ),
            bowlerScores = listOf(
                BowlerScore(player = openingBowler)
            ),
            currentStrikerId = striker.id,
            currentNonStrikerId = nonStriker.id,
            currentBowlerId = openingBowler.id,
            ballHistory = emptyList(),
            isCompleted = false
        )

        val newMatch = CricketMatch(
            id = "match_${System.currentTimeMillis()}",
            title = "${battingFirst.name} vs ${bowlingFirst.name}",
            teamA = teamA,
            teamB = teamB,
            matchType = matchType,
            ballType = ballType,
            totalOvers = totalOvers,
            venue = venue,
            tossWinner = tossWinner,
            tossDecision = tossDecision,
            status = MatchStatus.LIVE,
            currentInningIndex = 0,
            innings = listOf(firstInning),
            resultMessage = null,
            scorerId = _userProfile.value.id
        )

        // Auto-save both team squads into persistent player roster
        teamA.players.forEach { savePlayerToRoster(it) }
        teamB.players.forEach { savePlayerToRoster(it) }

        _currentMatch.value = newMatch
        _isSpectatorMode.value = false
        _matchesList.update { listOf(newMatch) + it }
        navigateTo(AppScreen.LiveScoring)
    }

    // Auto-commit match stats to persistent player roster when match finishes (Item 13)
    fun commitMatchStatsToPlayers(match: CricketMatch) {
        val result = match.resultMessage ?: "Match Completed"
        val allPlayersInMatch = (match.teamA.players + match.teamB.players).distinctBy { it.id }

        val allBalls = match.innings.flatMap { it.ballHistory }
        val allBatsmenScores = match.innings.flatMap { it.batsmenScores }
        val allBowlerScores = match.innings.flatMap { it.bowlerScores }

        allPlayersInMatch.forEach { player ->
            val bScore = allBatsmenScores.find { it.player.id == player.id }
            val bowScore = allBowlerScores.find { it.player.id == player.id }
            val catchesCount = allBalls.count {
                it.isWicket && it.dismissalType == DismissalType.CAUGHT && it.fielderName?.trim().equals(player.name.trim(), ignoreCase = true)
            }
            val runOutsCount = allBalls.count {
                it.isWicket && it.dismissalType == DismissalType.RUN_OUT && it.fielderName?.trim().equals(player.name.trim(), ignoreCase = true)
            }

            val playerTeam = if (match.teamA.players.any { it.id == player.id }) match.teamA.name else match.teamB.name
            val opponentTeam = if (match.teamA.players.any { it.id == player.id }) match.teamB.name else match.teamA.name

            val runs = bScore?.runs ?: 0
            val balls = bScore?.balls ?: 0
            val fours = bScore?.fours ?: 0
            val sixes = bScore?.sixes ?: 0
            val wickets = bowScore?.wickets ?: 0
            val runsConceded = bowScore?.runsConceded ?: 0
            val overs = bowScore?.oversString ?: "0.0"

            val matchRecord = com.example.data.PlayerMatchRecord(
                matchId = match.id,
                matchTitle = match.title,
                date = "Today",
                teamName = playerTeam,
                opponentName = opponentTeam,
                runs = runs,
                balls = balls,
                fours = fours,
                sixes = sixes,
                isOut = bScore?.isOut ?: false,
                wickets = wickets,
                runsConceded = runsConceded,
                oversBowled = overs,
                catches = catchesCount,
                runOuts = runOutsCount,
                result = result
            )

            // Update in persistent roster
            _players.update { list ->
                val idx = list.indexOfFirst {
                    it.id == player.id || (it.phone.isNotBlank() && it.phone == player.phone) || it.name.trim().equals(player.name.trim(), ignoreCase = true)
                }
                if (idx >= 0) {
                    val curr = list[idx]
                    val updated = curr.copy(
                        matches = curr.matches + 1,
                        runs = curr.runs + runs,
                        wickets = curr.wickets + wickets,
                        catches = curr.catches + catchesCount,
                        runOuts = curr.runOuts + runOutsCount,
                        teamsRepresented = (curr.teamsRepresented + playerTeam).distinct(),
                        matchHistory = listOf(matchRecord) + curr.matchHistory
                    )
                    list.toMutableList().apply { set(idx, updated) }
                } else {
                    val newP = player.copy(
                        matches = 1,
                        runs = runs,
                        wickets = wickets,
                        catches = catchesCount,
                        runOuts = runOutsCount,
                        teamsRepresented = listOf(playerTeam),
                        matchHistory = listOf(matchRecord)
                    )
                    list + newP
                }
            }

            // Update UserProfile if active user
            if (player.id == _userProfile.value.id || player.name.trim().equals(_userProfile.value.name.trim(), ignoreCase = true)) {
                _userProfile.update { u ->
                    u.copy(
                        matches = u.matches + 1,
                        runs = u.runs + runs,
                        wickets = u.wickets + wickets,
                        catches = u.catches + catchesCount,
                        runOuts = u.runOuts + runOutsCount,
                        fours = u.fours + fours,
                        sixes = u.sixes + sixes,
                        teamsRepresented = (u.teamsRepresented + playerTeam).distinct(),
                        matchHistory = listOf(matchRecord) + u.matchHistory
                    )
                }
            }
        }
    }

    private fun updateInningAndCheckMatchEnd(
        updatedInning: Inning,
        isOverEnd: Boolean,
        isInningDone: Boolean,
        isTargetReached: Boolean
    ) {
        val match = _currentMatch.value ?: return
        val updatedInnings = match.innings.toMutableList()
        updatedInnings[match.currentInningIndex] = updatedInning

        var matchResult: String? = match.resultMessage
        var matchStatus = match.status

        if (isInningDone) {
            if (match.currentInningIndex == 0) {
                // First innings ended!
                isMatchFinishedModalOpen.value = true
            } else {
                // Second innings ended -> Match complete!
                matchStatus = MatchStatus.COMPLETED
                val target = match.target ?: (updatedInnings[0].totalRuns + 1)
                matchResult = if (updatedInning.totalRuns >= target) {
                    val wicketsLeft = 10 - updatedInning.wickets
                    "${updatedInning.battingTeam.name} won by $wicketsLeft wickets!"
                } else if (updatedInning.totalRuns == target - 1) {
                    "Match Tied!"
                } else {
                    val runsDiff = (target - 1) - updatedInning.totalRuns
                    "${updatedInnings[0].battingTeam.name} won by $runsDiff runs!"
                }
                isMatchFinishedModalOpen.value = true
            }
        } else if (isOverEnd) {
            isOverCompleteModalOpen.value = true
        }

        val updatedMatch = match.copy(
            innings = updatedInnings,
            status = matchStatus,
            resultMessage = matchResult
        )

        // Automatically commit career stats when match finishes
        if (matchStatus == MatchStatus.COMPLETED) {
            commitMatchStatsToPlayers(updatedMatch)
        }

        _currentMatch.value = updatedMatch
        _matchesList.update { list ->
            list.map { if (it.id == updatedMatch.id) updatedMatch else it }
        }
    }
}
