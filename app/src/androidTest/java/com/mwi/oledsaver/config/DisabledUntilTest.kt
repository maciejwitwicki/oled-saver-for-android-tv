package com.mwi.oledsaver.config

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mwi.oledsaver.OledSaverApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@RunWith(AndroidJUnit4::class)
class DisabledUntilTest {

    private val settings =
        (ApplicationProvider.getApplicationContext<Context>() as OledSaverApplication).settingsRepository
    private val config = ConfigProvider()

    @After
    fun cleanup() = runBlocking {
        settings.setDisabledUntil(null)
        settings.setStartedDate(null)
    }

    @Test
    fun disabledUntil_roundTripsThroughDataStore() = runBlocking {
        val until = Instant.now().plus(Duration.ofMinutes(10)).truncatedTo(ChronoUnit.SECONDS)

        settings.setDisabledUntil(until)
        assertEquals(until, settings.disabledUntil.first())

        settings.setDisabledUntil(null)
        assertNull(settings.disabledUntil.first())
    }

    @Test
    fun decide_isDisabledWhileDeadlineIsInTheFuture() {
        val until = Instant.now().plus(Duration.ofMinutes(10))

        val decision = config.decide(started = Instant.EPOCH, disabledUntil = until)

        assertTrue("expected Disabled, got $decision", decision is LaunchDecision.Disabled)
        assertEquals(until, (decision as LaunchDecision.Disabled).until)
    }

    @Test
    fun decide_ignoresExpiredOrMissingDeadline() {
        val expired = Instant.now().minus(Duration.ofMinutes(1))

        assertFalse(config.decide(Instant.EPOCH, expired) is LaunchDecision.Disabled)
        assertFalse(config.decide(Instant.EPOCH, null) is LaunchDecision.Disabled)
    }

    @Test
    fun decide_disabledWinsOverStartedToday() {
        val until = Instant.now().plus(Duration.ofMinutes(10))

        assertTrue(config.decide(Instant.now(), until) is LaunchDecision.Disabled)
        assertEquals(LaunchDecision.Skip, config.decide(Instant.now(), null))
    }
}
