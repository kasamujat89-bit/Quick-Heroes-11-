package com.example.data

enum class ExtraType {
    WIDE,
    NO_BALL,
    BYE,
    LEG_BYE,
    PENALTY
}

enum class DismissalType(val label: String) {
    BOWLED("Bowled"),
    CAUGHT("Caught"),
    RUN_OUT("Run Out"),
    LBW("LBW"),
    STUMPED("Stumped"),
    HIT_WICKET("Hit Wicket"),
    RETIRED_HURT("Retired Hurt")
}

enum class MatchStatus {
    UPCOMING,
    LIVE,
    COMPLETED
}

data class Player(
    val id: String,
    val name: String,
    val city: String,
    val phone: String = "",
    val matches: Int = 0,
    val runs: Int = 0,
    val wickets: Int = 0,
    val isPro: Boolean = false,
    val isFollowing: Boolean = false,
    val role: String = "All-rounder", // Batsman, Bowler, All-rounder, Wk-Batsman
    val battingStyle: String = "Right-hand bat",
    val bowlingStyle: String = "Right-arm medium",
    val jerseyNumber: Int = 7
)

data class Team(
    val id: String,
    val name: String,
    val shortName: String,
    val city: String,
    val colorHex: Long = 0xFF1E3A8A,
    val players: List<Player> = emptyList()
)

data class BatsmanScore(
    val player: Player,
    val runs: Int = 0,
    val balls: Int = 0,
    val fours: Int = 0,
    val sixes: Int = 0,
    val isOut: Boolean = false,
    val dismissalText: String = "Not out",
    val isOnStrike: Boolean = false
) {
    val strikeRate: Float
        get() = if (balls > 0) (runs.toFloat() / balls) * 100f else 0f
}

data class BowlerScore(
    val player: Player,
    val legalBalls: Int = 0,
    val maidens: Int = 0,
    val runsConceded: Int = 0,
    val wickets: Int = 0
) {
    val oversString: String
        get() {
            val completedOvers = legalBalls / 6
            val ballsInCurrent = legalBalls % 6
            return "$completedOvers.$ballsInCurrent"
        }

    val economy: Float
        get() = if (legalBalls > 0) (runsConceded.toFloat() / (legalBalls / 6.0f)) else 0f
}

data class ExtrasBreakdown(
    val wides: Int = 0,
    val noBalls: Int = 0,
    val byes: Int = 0,
    val legByes: Int = 0,
    val penalty: Int = 0
) {
    val total: Int
        get() = wides + noBalls + byes + legByes + penalty
}

data class BallEvent(
    val id: String,
    val overNumber: Int,
    val ballInOver: Int,
    val runsScored: Int,
    val extraRuns: Int = 0,
    val extraType: ExtraType? = null,
    val isLegalBall: Boolean = true,
    val isWicket: Boolean = false,
    val dismissalType: DismissalType? = null,
    val dismissedPlayer: Player? = null,
    val fielderName: String? = null,
    val striker: Player,
    val nonStriker: Player,
    val bowler: Player,
    val displayText: String
)

data class Inning(
    val id: String,
    val inningNumber: Int, // 1 or 2
    val battingTeam: Team,
    val bowlingTeam: Team,
    val totalRuns: Int = 0,
    val wickets: Int = 0,
    val legalBalls: Int = 0,
    val extras: ExtrasBreakdown = ExtrasBreakdown(),
    val batsmenScores: List<BatsmanScore> = emptyList(),
    val bowlerScores: List<BowlerScore> = emptyList(),
    val currentStrikerId: String = "",
    val currentNonStrikerId: String = "",
    val currentBowlerId: String = "",
    val ballHistory: List<BallEvent> = emptyList(),
    val isCompleted: Boolean = false
) {
    val oversString: String
        get() {
            val ov = legalBalls / 6
            val b = legalBalls % 6
            return "$ov.$b"
        }

    val runRate: Float
        get() = if (legalBalls > 0) (totalRuns.toFloat() / (legalBalls / 6.0f)) else 0f

    val striker: BatsmanScore?
        get() = batsmenScores.find { it.player.id == currentStrikerId }

    val nonStriker: BatsmanScore?
        get() = batsmenScores.find { it.player.id == currentNonStrikerId }

    val activeBowler: BowlerScore?
        get() = bowlerScores.find { it.player.id == currentBowlerId }

    val currentOverNumber: Int
        get() = legalBalls / 6

    val currentOverBalls: List<BallEvent>
        get() {
            if (ballHistory.isEmpty()) return emptyList()
            return ballHistory.filter { it.overNumber == (legalBalls / 6) }
        }

    val recentBalls: List<BallEvent>
        get() {
            if (ballHistory.isEmpty()) return emptyList()
            val targetOver = if (legalBalls > 0 && legalBalls % 6 == 0 && ballHistory.lastOrNull()?.isLegalBall == true) {
                (legalBalls - 1) / 6
            } else {
                legalBalls / 6
            }
            return ballHistory.filter { it.overNumber == targetOver }
        }
}

enum class CelebrationType(val title: String, val subtitle: String, val colorHex: Long) {
    FOUR("FOUR! ૪ રન!", "શાનદાર ચોગ્ગો! બોલ બાઉન્ડ્રી પાર!", 0xFF2563EB),
    SIX("SIX! ૬ રન!", "ગગનચુંબી છગ્ગો! બોલ સીધો સ્ટેન્ડમાં!", 0xFF16A34A),
    WICKET("WICKET! વિકેટ!", "ખેલાડી આઉટ! બોલરને મોટી સફળતા!", 0xFFDC2626)
}

data class CricketMatch(
    val id: String,
    val title: String,
    val teamA: Team,
    val teamB: Team,
    val matchType: String = "Limited Overs",
    val ballType: String = "Tennis Ball",
    val totalOvers: Int = 10,
    val oversPerBowler: Int = 2,
    val venue: String = "Naliya Cricket Ground",
    val tossWinner: Team? = null,
    val tossDecision: String = "Bat", // "Bat" or "Bowl"
    val status: MatchStatus = MatchStatus.LIVE,
    val currentInningIndex: Int = 0, // 0 for 1st inning, 1 for 2nd inning
    val innings: List<Inning> = emptyList(),
    val resultMessage: String? = null,
    val scorerId: String = "user_kasam_jat"
) {
    val currentInning: Inning?
        get() = innings.getOrNull(currentInningIndex)

    val target: Int?
        get() = if (currentInningIndex == 1 && innings.isNotEmpty()) innings[0].totalRuns + 1 else null

    val requiredRuns: Int?
        get() = target?.let { it - (currentInning?.totalRuns ?: 0) }

    val remainingBalls: Int?
        get() {
            if (currentInningIndex != 1) return null
            val ballsBowled = currentInning?.legalBalls ?: 0
            val maxBalls = totalOvers * 6
            return (maxBalls - ballsBowled).coerceAtLeast(0)
        }

    val requiredRunRate: Float?
        get() {
            val req = requiredRuns ?: return null
            val rem = remainingBalls ?: return null
            return if (rem > 0) (req.toFloat() / (rem / 6.0f)) else null
        }
}

data class LookingItem(
    val id: String,
    val category: String, // "Player", "Team", "Ground", "Umpire"
    val title: String,
    val subtitle: String,
    val location: String,
    val postedBy: String,
    val timeAgo: String,
    val contactPhone: String
)

data class Tournament(
    val id: String,
    val name: String,
    val city: String,
    val teamsCount: Int,
    val startDate: String,
    val prizeMoney: String,
    val bannerColor: Long = 0xFFDC2626
)

data class UserProfile(
    val id: String = "user_kasam_jat",
    val name: String = "Kasam Jat",
    val title: String = "ઓલરાઉન્ડર અને કેપ્ટન (Captain & All-rounder)",
    val phone: String = "8980294520",
    val city: String = "Naliya (Kutch)",
    val photoUri: String? = null,
    val avatarPreset: Int = 0,
    val matches: Int = 34,
    val runs: Int = 1240,
    val wickets: Int = 48,
    val highestScore: String = "88*",
    val strikeRate: Float = 145.2f,
    val battingAvg: Float = 44.3f,
    val bestBowling: String = "4/16",
    val bowlingEconomy: Float = 6.4f,
    val fifties: Int = 8,
    val hundreds: Int = 2,
    val fours: Int = 112,
    val sixes: Int = 54
)

