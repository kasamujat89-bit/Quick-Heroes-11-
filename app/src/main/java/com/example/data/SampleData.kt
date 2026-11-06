package com.example.data

object SampleData {
    // Current user (from Screenshot 3)
    val currentUser = Player(
        id = "user_karam_khan",
        name = "Karam Khan",
        city = "Naliya",
        matches = 420,
        runs = 9840,
        wickets = 312,
        isPro = true,
        isFollowing = false,
        role = "All-rounder",
        battingStyle = "Right-hand bat",
        bowlingStyle = "Right-arm fast medium",
        jerseyNumber = 10
    )

    // Players list matching Screenshot 2
    val players = listOf(
        Player(
            id = "p1",
            name = "Abuahemad Jat",
            city = "Naliya",
            matches = 976,
            runs = 19336,
            wickets = 914,
            isPro = true,
            isFollowing = false,
            role = "Top-order Batsman",
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm off break",
            jerseyNumber = 18
        ),
        Player(
            id = "p2",
            name = "Hasan.a.Jat",
            city = "Naliya",
            matches = 829,
            runs = 13276,
            wickets = 599,
            isPro = false,
            isFollowing = false,
            role = "All-rounder",
            battingStyle = "Left-hand bat",
            bowlingStyle = "Slow left-arm orthodox",
            jerseyNumber = 7
        ),
        Player(
            id = "p3",
            name = "Latif Jat ( L-J )",
            city = "Naliya",
            matches = 794,
            runs = 8825,
            wickets = 616,
            isPro = false,
            isFollowing = false,
            role = "Bowler",
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm fast",
            jerseyNumber = 99
        ),
        Player(
            id = "p4",
            name = "M s jat",
            city = "Naliya",
            matches = 758,
            runs = 7108,
            wickets = 480,
            isPro = false,
            isFollowing = false,
            role = "Wk-Batsman",
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm medium",
            jerseyNumber = 45
        ),
        Player(
            id = "p5",
            name = "Hussain A",
            city = "Naliya",
            matches = 512,
            runs = 6240,
            wickets = 340,
            isPro = false,
            isFollowing = false,
            role = "Batsman",
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm leg break",
            jerseyNumber = 12
        ),
        Player(
            id = "p6",
            name = "Imran Pathan",
            city = "Bhuj",
            matches = 410,
            runs = 8900,
            wickets = 215,
            isPro = true,
            isFollowing = true,
            role = "All-rounder",
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm medium fast",
            jerseyNumber = 33
        ),
        Player(
            id = "p7",
            name = "Hardik Solanki",
            city = "Rajkot",
            matches = 320,
            runs = 5400,
            wickets = 430,
            isPro = false,
            isFollowing = false,
            role = "Bowler",
            battingStyle = "Right-hand bat",
            bowlingStyle = "Right-arm off break",
            jerseyNumber = 24
        ),
        Player(
            id = "p8",
            name = "Suresh Jadeja",
            city = "Gandhidham",
            matches = 295,
            runs = 4820,
            wickets = 180,
            isPro = false,
            isFollowing = false,
            role = "Batsman",
            battingStyle = "Left-hand bat",
            bowlingStyle = "Left-arm medium",
            jerseyNumber = 11
        )
    )

    val teamNaliya = Team(
        id = "team_naliya",
        name = "Naliya Royals",
        shortName = "NR",
        city = "Naliya",
        colorHex = 0xFFDC2626,
        players = listOf(players[0], players[1], players[2], players[3], players[4], currentUser)
    )

    val teamKutch = Team(
        id = "team_kutch",
        name = "Kutch Warriors",
        shortName = "KW",
        city = "Bhuj",
        colorHex = 0xFF0D9488,
        players = listOf(players[5], players[6], players[7])
    )

    val teamAhmedabad = Team(
        id = "team_ahmedabad",
        name = "Ahmedabad Strikers",
        shortName = "AS",
        city = "Ahmedabad",
        colorHex = 0xFF2563EB,
        players = emptyList()
    )

    val teamSaurashtra = Team(
        id = "team_saurashtra",
        name = "Saurashtra Lions",
        shortName = "SL",
        city = "Rajkot",
        colorHex = 0xFFD97706,
        players = emptyList()
    )

    fun createInitialLiveMatch(): CricketMatch {
        val strikerPlayer = players[0] // Abu Jat
        val nonStrikerPlayer = players[1] // Hasan Jat
        val bowlerPlayer = players[6] // Hardik Solanki

        val batsmenScores = listOf(
            BatsmanScore(player = strikerPlayer, runs = 38, balls = 22, fours = 4, sixes = 2, isOnStrike = true),
            BatsmanScore(player = nonStrikerPlayer, runs = 27, balls = 16, fours = 3, sixes = 1, isOnStrike = false),
            BatsmanScore(player = players[3], runs = 12, balls = 8, fours = 2, sixes = 0, isOut = true, dismissalText = "c Imran b Hardik")
        )

        val bowlerScores = listOf(
            BowlerScore(player = bowlerPlayer, legalBalls = 14, maidens = 0, runsConceded = 24, wickets = 1),
            BowlerScore(player = players[5], legalBalls = 12, maidens = 0, runsConceded = 18, wickets = 0)
        )

        val initialHistory = listOf(
            BallEvent(
                id = "b1", overNumber = 4, ballInOver = 1, runsScored = 1,
                striker = strikerPlayer, nonStriker = nonStrikerPlayer, bowler = bowlerPlayer,
                displayText = "1"
            ),
            BallEvent(
                id = "b2", overNumber = 4, ballInOver = 2, runsScored = 4,
                striker = nonStrikerPlayer, nonStriker = strikerPlayer, bowler = bowlerPlayer,
                displayText = "4"
            ),
            BallEvent(
                id = "b3", overNumber = 4, ballInOver = 3, runsScored = 0,
                striker = nonStrikerPlayer, nonStriker = strikerPlayer, bowler = bowlerPlayer,
                displayText = "0"
            ),
            BallEvent(
                id = "b4", overNumber = 4, ballInOver = 4, runsScored = 6,
                striker = nonStrikerPlayer, nonStriker = strikerPlayer, bowler = bowlerPlayer,
                displayText = "6"
            ),
            BallEvent(
                id = "b5", overNumber = 4, ballInOver = 5, runsScored = 0, extraRuns = 1, extraType = ExtraType.WIDE,
                isLegalBall = false, striker = nonStrikerPlayer, nonStriker = strikerPlayer, bowler = bowlerPlayer,
                displayText = "Wd"
            ),
            BallEvent(
                id = "b6", overNumber = 4, ballInOver = 5, runsScored = 1,
                striker = nonStrikerPlayer, nonStriker = strikerPlayer, bowler = bowlerPlayer,
                displayText = "1"
            )
        )

        val firstInning = Inning(
            id = "inn_1",
            inningNumber = 1,
            battingTeam = teamNaliya,
            bowlingTeam = teamKutch,
            totalRuns = 84,
            wickets = 1,
            legalBalls = 26, // 4.2 overs
            extras = ExtrasBreakdown(wides = 3, noBalls = 1, legByes = 2, byes = 0),
            batsmenScores = batsmenScores,
            bowlerScores = bowlerScores,
            currentStrikerId = strikerPlayer.id,
            currentNonStrikerId = nonStrikerPlayer.id,
            currentBowlerId = bowlerPlayer.id,
            ballHistory = initialHistory,
            isCompleted = false
        )

        return CricketMatch(
            id = "match_live_01",
            title = "Naliya Premier League 2026 - Match 4",
            teamA = teamNaliya,
            teamB = teamKutch,
            matchType = "Limited Overs",
            ballType = "Tennis Ball",
            totalOvers = 10,
            oversPerBowler = 2,
            venue = "Naliya Cricket Ground",
            tossWinner = teamNaliya,
            tossDecision = "Bat",
            status = MatchStatus.LIVE,
            currentInningIndex = 0,
            innings = listOf(firstInning),
            resultMessage = null
        )
    }

    val completedMatches: List<CricketMatch> = emptyList()

    val tournaments = listOf(
        Tournament(
            id = "t1",
            name = "Kutch Night Trophy 2026",
            city = "Bhuj",
            teamsCount = 16,
            startDate = "15 Nov 2026",
            prizeMoney = "Winner Trophy & Medals",
            entryFee = "100% Free Entry",
            bannerColor = 0xFFDC2626
        ),
        Tournament(
            id = "t2",
            name = "Naliya Super League - Season 5",
            city = "Naliya",
            teamsCount = 12,
            startDate = "01 Dec 2026",
            prizeMoney = "Champions Trophy & Honors",
            entryFee = "100% Free Entry",
            bannerColor = 0xFF0D9488
        ),
        Tournament(
            id = "t3",
            name = "Gujarat Grassroots Championship",
            city = "Ahmedabad",
            teamsCount = 24,
            startDate = "20 Dec 2026",
            prizeMoney = "Gold Trophy & Certificates",
            entryFee = "100% Free Entry",
            bannerColor = 0xFF0F766E
        )
    )

    val lookingPosts = listOf(
        LookingItem(
            id = "l1",
            category = "Player",
            title = "Need 2 Fast Bowlers for Weekend Match",
            subtitle = "Leather ball match this Sunday 8:00 AM. Refreshments provided.",
            location = "Naliya Ground",
            postedBy = "Hasan Jat",
            timeAgo = "2h ago",
            contactPhone = "9876543210"
        ),
        LookingItem(
            id = "l2",
            category = "Umpire",
            title = "Umpire required for Semi-Finals",
            subtitle = "Certified or experienced umpire needed for 20-over match. Match fee ₹1500.",
            location = "Bhuj Gymkhana",
            postedBy = "Kutch Warriors Club",
            timeAgo = "4h ago",
            contactPhone = "9825123456"
        ),
        LookingItem(
            id = "l3",
            category = "Ground",
            title = "Floodlit Box Cricket Turf Available for Booking",
            subtitle = "Slots available on Tuesday & Friday nights. High-speed cameras for live streaming.",
            location = "Gandhidham Sports Arena",
            postedBy = "Arena Admin",
            timeAgo = "1d ago",
            contactPhone = "9898001122"
        ),
        LookingItem(
            id = "l4",
            category = "Team",
            title = "Looking for friendly match opponent",
            subtitle = "15-over tennis ball match. Pitch ready, looking for competitive team.",
            location = "Mundra Cricket Oval",
            postedBy = "Mundra Tigers",
            timeAgo = "1d ago",
            contactPhone = "9723456789"
        )
    )

    // Digital Badges & Medals Seed Data (Requirement)
    val standardBadges = listOf(
        DigitalBadge(
            id = "b_fifty",
            title = "Half-Century Star",
            titleGujarati = "અર્ધશતક સ્ટાર (૫૦ રન)",
            description = "Scored 50+ runs in a high-pressure match",
            category = "Batting",
            iconSymbol = "🏏",
            badgeColor = 0xFFF59E0B, // Gold
            dateUnlocked = "Unlocked",
            isUnlocked = true
        ),
        DigitalBadge(
            id = "b_century",
            title = "Century Legend",
            titleGujarati = "શતક લિજેન્ડ (૧૦૦ રન)",
            description = "Magnificent 100+ runs single innings milestone",
            category = "Batting",
            iconSymbol = "👑",
            badgeColor = 0xFFEF4444, // Red Ruby
            dateUnlocked = "Unlocked",
            isUnlocked = true
        ),
        DigitalBadge(
            id = "b_fifer",
            title = "Fifer Hero (5 Wkts)",
            titleGujarati = "૫ વિકેટ હીરો",
            description = "Demolished opposition taking 5 wickets in an innings",
            category = "Bowling",
            iconSymbol = "🔥",
            badgeColor = 0xFF8B5CF6, // Purple
            dateUnlocked = "Unlocked",
            isUnlocked = true
        ),
        DigitalBadge(
            id = "b_mom",
            title = "Player of the Match",
            titleGujarati = "પ્લેયર ઓફ ધ મેચ",
            description = "Awarded Man of the Match for match-winning heroics",
            category = "Special",
            iconSymbol = "🏆",
            badgeColor = 0xFF10B981, // Emerald
            dateUnlocked = "Unlocked",
            isUnlocked = true
        ),
        DigitalBadge(
            id = "b_hattrick",
            title = "Hat-trick Hunter",
            titleGujarati = "હેટ્રિક હન્ટર",
            description = "3 wickets in 3 consecutive balls",
            category = "Bowling",
            iconSymbol = "⚡",
            badgeColor = 0xFF06B6D4, // Cyan
            dateUnlocked = "Locked",
            isUnlocked = false
        ),
        DigitalBadge(
            id = "b_sixer",
            title = "Sixer King (5+ Sixes)",
            titleGujarati = "સિક્સર કિંગ (૫+ છગ્ગા)",
            description = "Smashed 5 or more massive sixes in one innings",
            category = "Batting",
            iconSymbol = "🎯",
            badgeColor = 0xFFF97316, // Orange
            dateUnlocked = "Unlocked",
            isUnlocked = true
        ),
        DigitalBadge(
            id = "b_maiden",
            title = "Iron Maiden Over",
            titleGujarati = "આયર્ન મેડન ઓવર",
            description = "Bowled a maiden over conceding 0 runs",
            category = "Bowling",
            iconSymbol = "🛡️",
            badgeColor = 0xFF3B82F6, // Blue
            dateUnlocked = "Unlocked",
            isUnlocked = true
        ),
        DigitalBadge(
            id = "b_safe_hands",
            title = "Safe Hands (3+ Catches)",
            titleGujarati = "સેફ હેન્ડ્સ (૩+ કેચ)",
            description = "Took 3+ stunning catches in a single match",
            category = "Fielding",
            iconSymbol = "🧤",
            badgeColor = 0xFFEC4899, // Pink
            dateUnlocked = "Unlocked",
            isUnlocked = true
        )
    )

    // Player Marketplace / Urgent Requirements (Requirement)
    val samplePlayerRequirements = listOf(
        PlayerRequirement(
            id = "req_1",
            teamName = "Naliya Royals",
            roleNeeded = "Fast Bowler",
            matchDate = "Sunday, 08 Nov • 7:30 AM",
            groundCity = "Naliya High School Ground",
            oversType = "20 Overs Leather Ball",
            contactPhone = "8980294520",
            urgent = true,
            postedBy = "Capt. Kasam Jat",
            applicantsCount = 3,
            timeAgo = "10 mins ago"
        ),
        PlayerRequirement(
            id = "req_2",
            teamName = "Kutch Strikers",
            roleNeeded = "Wicketkeeper Batsman",
            matchDate = "Saturday, 07 Nov • 1:30 PM",
            groundCity = "Bhuj Gymkhana Ground",
            oversType = "16 Overs Tennis Ball",
            contactPhone = "9825123456",
            urgent = true,
            postedBy = "Capt. Imran Baloch",
            applicantsCount = 5,
            timeAgo = "25 mins ago"
        ),
        PlayerRequirement(
            id = "req_3",
            teamName = "Gandhidham Lions",
            roleNeeded = "Top-Order Batsman",
            matchDate = "Sunday, 08 Nov • 9:00 AM",
            groundCity = "Gandhidham Cricket Oval",
            oversType = "20 Overs Leather Ball",
            contactPhone = "9898001122",
            urgent = false,
            postedBy = "Capt. Hitesh Patel",
            applicantsCount = 2,
            timeAgo = "1 hour ago"
        ),
        PlayerRequirement(
            id = "req_4",
            teamName = "Mundra Warriors",
            roleNeeded = "Spin Bowler",
            matchDate = "Tuesday, 10 Nov • 3:00 PM",
            groundCity = "Mundra Port Ground",
            oversType = "15 Overs Tennis Ball",
            contactPhone = "9723456789",
            urgent = true,
            postedBy = "Capt. Dilip Jadeja",
            applicantsCount = 1,
            timeAgo = "2 hours ago"
        )
    )

    // Live Auto-Commentary Feed (Requirement)
    val sampleCommentaryList = listOf(
        CommentaryItem(
            id = "c1",
            overText = "14.2",
            headline = "CRACKING SIX! Out of the park!",
            commentaryGujarati = "હવામાં શાનદાર શોટ અને આ સીધા 6 રન! બોલ સીધો દર્શકોની વચ્ચે!",
            commentaryHindi = "हवा में खूबसूरत शॉट और गेंद सीधे दर्शकों के बीच 6 रन के लिए!",
            commentaryEnglish = "What a magnificent towering shot into the stands for a huge SIX!",
            bowlerName = "Sajid Jat",
            batsmanName = "Kasam Jat",
            runs = 6,
            isSix = true,
            timestamp = "Just now"
        ),
        CommentaryItem(
            id = "c2",
            overText = "14.1",
            headline = "FOUR! Pierces the cover gap!",
            commentaryGujarati = "ચાર રન! ગેપમાં સુંદર કટ શોટ અને બોલ સીધો બાઉન્ડ્રી લાઈન બહાર!",
            commentaryHindi = "चार रन! गैप में शानदार प्लेसमेंट और गेंद गोली की रफ्तार से बाउंड्री पार!",
            commentaryEnglish = "CRACKING FOUR! Pierces the cover field with sublime timing to the fence!",
            bowlerName = "Sajid Jat",
            batsmanName = "Kasam Jat",
            runs = 4,
            isBoundary = true,
            timestamp = "1 min ago"
        ),
        CommentaryItem(
            id = "c3",
            overText = "13.6",
            headline = "Single to deep midwicket",
            commentaryGujarati = "હળવા હાથે ટેપ કરીને ઝડપી સિંગલ રન પૂર્ણ કર્યો.",
            commentaryHindi = "हल्के हाथों से खेला और तेजी से दौड़कर एक रन पूरा किया.",
            commentaryEnglish = "Tapped gently into the leg side, easy single taken.",
            bowlerName = "Karam Khan",
            batsmanName = "Irfan Pathan",
            runs = 1,
            timestamp = "2 mins ago"
        ),
        CommentaryItem(
            id = "c4",
            overText = "13.5",
            headline = "WICKET! Clean Bowled!",
            commentaryGujarati = "આઉટ! મોટી સફળતા! બોલરે સ્ટમ્પ ઉડાવીને ક્લીન બોલ્ડ કરી દીધો!",
            commentaryHindi = "विकेट! बड़ा झटका! क्लीन बोल्ड होकर बल्लेबाज पवेलियन लौटे!",
            commentaryEnglish = "WICKET! Stumps shattered! A searing yorker cleans him up completely!",
            bowlerName = "Karam Khan",
            batsmanName = "Hasan Jat",
            runs = 0,
            isWicket = true,
            timestamp = "3 mins ago"
        )
    )

    // 100% Free Fantasy League Standings (Requirement)
    val sampleFantasyLeaderboard = listOf(
        FantasyLeaderboardEntry(1, "Kasam Jat (You)", "Naliya Super Kings XI", "Kasam Jat (C)", 482, isCurrentUser = true),
        FantasyLeaderboardEntry(2, "Hasan Baloch", "Kutch Thunder XI", "Irfan Pathan (C)", 465),
        FantasyLeaderboardEntry(3, "Imran Khan", "Desert Storm 11", "Karam Khan (C)", 448),
        FantasyLeaderboardEntry(4, "Farooq Jat", "Naliya Express XI", "Sajid Jat (C)", 420),
        FantasyLeaderboardEntry(5, "Irfan Sandh", "Royal Grassroots 11", "Kasam Jat (C)", 398)
    )
}

