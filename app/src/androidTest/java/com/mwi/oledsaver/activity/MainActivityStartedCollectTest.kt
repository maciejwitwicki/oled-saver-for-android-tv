package com.mwi.oledsaver.activity

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.config.ConfigProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Reproduces the re-entrancy in MainActivity.handleLaunch():
 *
 *   settings.started.collect { started ->
 *       if (isEnabled(started)) { ...; settings.setStartedDate(Instant.now()); finish() }
 *       else { finishAndRemoveTask() }
 *   }
 *
 * `settings.started` is backed by DataStore.data, which re-emits after every write.
 * Writing setStartedDate() from inside the collector therefore triggers a second pass,
 * and because the new value is "today", ConfigProvider.isEnabled() is false on that pass,
 * so the else-branch (finishAndRemoveTask) runs right after finish().
 *
 * Runs against the real SettingsRepository (real DataStore file) and the real ConfigProvider.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityStartedCollectTest {

    // Reuse the app's single repository: DataStore forbids two instances on the same file.
    private val settings =
        (ApplicationProvider.getApplicationContext<Context>() as OledSaverApplication).settingsRepository
    private val config = ConfigProvider()

    @Before
    fun resetStartedDate() = runBlocking { settings.setStartedDate(null) }

    @After
    fun clearStartedDate() = runBlocking { settings.setStartedDate(null) }

    @Test
    fun collect_isReenteredAfterSetStartedDate_andSecondPassTakesElseBranch() = runBlocking {
        val emissions = mutableListOf<Instant>()
        val enabledPerPass = mutableListOf<Boolean>()

        val collector = launch {
            settings.started.collect { started ->
                emissions += started
                enabledPerPass += config.isEnabled(started = started)
                if (emissions.size == 1) {
                    // Mirror the "conditions met" branch of MainActivity.
                    settings.setStartedDate(Instant.now())
                }
            }
        }

        withTimeout(5_000) { while (emissions.size < 2) delay(20) }
        delay(1_000) // give DataStore a chance to emit anything further
        collector.cancel()

        assertEquals("collector must run exactly twice (initial + re-emission)", 2, emissions.size)
        assertEquals("first pass sees the cleared value", Instant.EPOCH, emissions[0])
        assertEquals(
            "second pass sees the date written from inside the collector",
            LocalDate.now(),
            emissions[1].atZone(ZoneId.systemDefault()).toLocalDate()
        )
        assertFalse(
            "second pass: isEnabled is false (started today) so finishAndRemoveTask() would run",
            enabledPerPass[1]
        )
    }
}
