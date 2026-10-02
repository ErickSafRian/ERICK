package com.example

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.model.CelebrationType
import com.example.viewmodel.DindongViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class CelebrationOverlayTest {

    private lateinit var application: Application
    private lateinit var viewModel: DindongViewModel

    @Before
    fun setUp() {
        application = ApplicationProvider.getApplicationContext()
        viewModel = DindongViewModel(application)
    }

    @Test
    fun testJackpotCelebrationTrigger() = runTest {
        assertNull(viewModel.uiState.value.activeCelebration)

        viewModel.triggerDemoJackpot()

        val celebration = viewModel.uiState.value.activeCelebration
        assertNotNull(celebration)
        assertTrue(celebration is CelebrationType.Jackpot)
        val jackpot = celebration as CelebrationType.Jackpot
        assertEquals(500L, jackpot.winAmount)
        assertEquals("🐉", jackpot.symbolEmoji)

        viewModel.dismissCelebration()
        assertNull(viewModel.uiState.value.activeCelebration)
    }

    @Test
    fun testDoubleUpCelebrationTrigger() = runTest {
        assertNull(viewModel.uiState.value.activeCelebration)

        viewModel.triggerDemoDoubleUp()

        val celebration = viewModel.uiState.value.activeCelebration
        assertNotNull(celebration)
        assertTrue(celebration is CelebrationType.DoubleUpSuccess)
        val doubleUp = celebration as CelebrationType.DoubleUpSuccess
        assertEquals(200L, doubleUp.newPool)
        assertEquals(100L, doubleUp.wonAmount)
        assertEquals(1, doubleUp.roundStreak)

        viewModel.continueDoubleUp()
        assertNull(viewModel.uiState.value.activeCelebration)
        assertTrue(viewModel.uiState.value.isDoubleUpOpen)

        // Test Cash Out
        viewModel.triggerDemoDoubleUp()
        viewModel.cashOutDoubleUp()
        assertNull(viewModel.uiState.value.activeCelebration)
        assertTrue(!viewModel.uiState.value.isDoubleUpOpen)
    }

    @Test
    fun testDoubleUpSuccessFlow() = runTest {
        var wonAtLeastOnce = false
        for (i in 0 until 20) {
            viewModel.triggerDemoDoubleUp()
            viewModel.dismissCelebration()

            viewModel.playDoubleUpGuess(true)
            if (viewModel.uiState.value.doubleUpResultSuccess == true) {
                wonAtLeastOnce = true
                val celebration = viewModel.uiState.value.activeCelebration
                assertNotNull("Celebration must be active on double up success", celebration)
                assertTrue(celebration is CelebrationType.DoubleUpSuccess)
                break
            }
        }
        assertTrue("Expected double up round to succeed within trials", wonAtLeastOnce)
    }
}
