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

import com.example.data.UserProfile

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
}

class CricketViewModel : ViewModel() {

    // User Profile State (Editable Name, Title, Photo, Matches, Runs, Wickets, etc.)
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

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

    // Active Live Match (Initialized with sample live community match so users can spectate or score immediately)
    private val _currentMatch = MutableStateFlow<CricketMatch?>(SampleData.createInitialLiveMatch())
    val currentMatch: StateFlow<CricketMatch?> = _currentMatch.asStateFlow()

    // Matches List
    private val _matchesList = MutableStateFlow<List<CricketMatch>>(
        listOf(SampleData.createInitialLiveMatch()) + SampleData.completedMatches
    )
    val matchesList: StateFlow<List<CricketMatch>> = _matchesList.asStateFlow()

    // Players list
    private val _players = MutableStateFlow<List<Player>>(SampleData.players)
    val players: StateFlow<List<Player>> = _players.asStateFlow()

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
            resultMessage = null
        )

        _currentMatch.value = newMatch
        _isSpectatorMode.value = false
        _matchesList.update { listOf(newMatch) + it }
        navigateTo(AppScreen.LiveScoring)
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

        _currentMatch.value = updatedMatch
        _matchesList.update { list ->
            list.map { if (it.id == updatedMatch.id) updatedMatch else it }
        }
    }
}
