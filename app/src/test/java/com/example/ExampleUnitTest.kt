package com.example

import com.example.data.SampleData
import com.example.viewmodel.CricketViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun cricketScoring_andUndo_worksCorrectly() {
    val viewModel = CricketViewModel()
    // Verify initially live community match is available to spectate
    org.junit.Assert.assertNotNull(viewModel.currentMatch.value)

    // User starts a new match with teams and players
    viewModel.createAndStartMatch(
        teamA = SampleData.teamNaliya,
        teamB = SampleData.teamKutch,
        totalOvers = 10,
        matchType = "Limited Overs",
        ballType = "Tennis Ball",
        venue = "Naliya Cricket Ground",
        tossWinner = SampleData.teamNaliya,
        tossDecision = "Bat",
        striker = SampleData.players[0],
        nonStriker = SampleData.players[1],
        openingBowler = SampleData.players[6]
    )

    // Initially newly started match has 0 runs
    val initialScore = viewModel.currentMatch.value?.currentInning?.totalRuns ?: 0
    assertEquals(0, initialScore)

    // Record runs: 4
    viewModel.recordRuns(4)
    val afterFour = viewModel.currentMatch.value?.currentInning?.totalRuns ?: 0
    assertEquals(4, afterFour)

    // Undo last ball
    viewModel.undoLastBall()
    val afterUndo = viewModel.currentMatch.value?.currentInning?.totalRuns ?: 0
    assertEquals(0, afterUndo)
  }
}
