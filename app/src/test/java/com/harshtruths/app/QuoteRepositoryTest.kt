package com.harshtruths.app

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.harshtruths.app.data.QuoteRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuoteRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        val prefs = context.getSharedPreferences("harsh_truths_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().commit()
    }

    @Test
    fun testFirstLaunchOpensQuote1Of99() {
        val repo = QuoteRepository(context)
        val (current, total) = repo.getProgress()
        assertEquals(1, current)
        assertEquals(99, total)
        assertEquals(1, repo.getSavedQuoteId())
    }

    @Test
    fun testNavigateNextAndPrevious() {
        val repo = QuoteRepository(context)
        // Next -> Quote 2
        repo.navigateToNext()
        assertEquals(2, repo.getSavedQuoteId())

        // Previous -> Quote 1
        repo.navigateToPrevious()
        assertEquals(1, repo.getSavedQuoteId())

        // Previous at 1 stays at 1
        repo.navigateToPrevious()
        assertEquals(1, repo.getSavedQuoteId())
    }

    @Test
    fun testPersistentReadingPositionRestored() {
        val repo1 = QuoteRepository(context)
        repo1.setCurrentQuoteId(7)
        assertEquals(7, repo1.getSavedQuoteId())

        // Simulate app reopen
        val repo2 = QuoteRepository(context)
        assertEquals(7, repo2.getSavedQuoteId())
        val (current, total) = repo2.getProgress()
        assertEquals(7, current)
        assertEquals(99, total)
    }

    @Test
    fun testDateChangeDoesNotOverwritePosition() {
        val repo1 = QuoteRepository(context)
        repo1.setCurrentQuoteId(12)

        // Advance start date artificially
        val prefs = context.getSharedPreferences("harsh_truths_prefs", Context.MODE_PRIVATE)
        val pastDate = System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000L)
        prefs.edit().putLong("start_date", pastDate).commit()

        // Simulate app reopen on next calendar day / future date
        val repo2 = QuoteRepository(context)
        assertEquals(12, repo2.getSavedQuoteId())
    }

    @Test
    fun testMaxQuoteBounds() {
        val repo = QuoteRepository(context)
        repo.setCurrentQuoteId(99)
        assertEquals(99, repo.getSavedQuoteId())

        // Next at 99 stays at 99
        repo.navigateToNext()
        assertEquals(99, repo.getSavedQuoteId())
    }

    @Test
    fun testGetTodayQuoteDoesNotOverwriteSavedPosition() {
        val repo = QuoteRepository(context)
        repo.setCurrentQuoteId(15)

        val quote = repo.getTodayQuote()
        assertNotNull(quote)
        assertEquals(15, quote?.id)
        assertEquals(15, repo.getSavedQuoteId())
    }
}
