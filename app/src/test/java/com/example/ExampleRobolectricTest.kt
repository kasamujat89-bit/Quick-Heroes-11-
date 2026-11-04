package com.example

import com.example.viewmodel.AppScreen
import com.example.viewmodel.CricketViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleRobolectricTest {
    @Test
    fun appInitializationCheck() {
        val viewModel = CricketViewModel()
        assertNotNull(viewModel)
        // Verify app starts on SplashScreen showing Quick Heroes 11 logo
        assertEquals(AppScreen.Splash, viewModel.currentScreen)
        // Verify finishSplash transitions to Home screen
        viewModel.finishSplash()
        assertEquals(AppScreen.Home, viewModel.currentScreen)
    }

    @Test
    fun testNavigationStack() {
        val viewModel = CricketViewModel()
        viewModel.finishSplash()
        viewModel.navigateTo(AppScreen.MatchSetup)
        assertEquals(AppScreen.MatchSetup, viewModel.currentScreen)
        viewModel.navigateBack()
        assertEquals(AppScreen.Home, viewModel.currentScreen)
    }

    @Test
    fun testSpectatorVsScorerMode() {
        val viewModel = CricketViewModel()
        val match = viewModel.currentMatch.value
        assertNotNull(match)

        // Open match as spectator (Read-only, no scoring keypad)
        viewModel.openMatchAsSpectator(match!!)
        assertEquals(AppScreen.LiveScoring, viewModel.currentScreen)
        assertTrue(viewModel.isSpectatorMode.value)

        // Switch role to scorer mode
        viewModel.setSpectatorMode(false)
        assertEquals(false, viewModel.isSpectatorMode.value)
    }

    @Test
    fun testUserProfileUpdate() {
        val viewModel = CricketViewModel()
        viewModel.updateUserProfile(
            name = "Kasam Jat",
            title = "ઓલરાઉન્ડર અને કેપ્ટન",
            phone = "8980294520",
            city = "Naliya",
            matches = 35,
            runs = 1280,
            wickets = 50,
            highestScore = "92*",
            bestBowling = "5/12"
        )

        val profile = viewModel.userProfile.value
        assertEquals("Kasam Jat", profile.name)
        assertEquals("ઓલરાઉન્ડર અને કેપ્ટન", profile.title)
        assertEquals(35, profile.matches)
        assertEquals(1280, profile.runs)
        assertEquals(50, profile.wickets)

        viewModel.updateProfilePhoto("content://media/external/images/media/12345")
        assertEquals("content://media/external/images/media/12345", viewModel.userProfile.value.photoUri)

        viewModel.navigateTo(AppScreen.Profile)
        assertEquals(AppScreen.Profile, viewModel.currentScreen)
    }
}

