package com.mwi.oledsaver.config

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mwi.oledsaver.OledSaverApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/** DataStore persistence of the dismissal deadline. Decision logic is tested in :core. */
@RunWith(AndroidJUnit4::class)
class DisabledUntilTest {

    private val settings =
        (ApplicationProvider.getApplicationContext<Context>() as OledSaverApplication).settingsRepository

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
}
