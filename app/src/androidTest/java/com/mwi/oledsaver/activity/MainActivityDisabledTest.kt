package com.mwi.oledsaver.activity

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.mwi.oledsaver.OledSaverApplication
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Launching MainActivity inside the dismissal window must finish without starting the
 * masker, keep the deadline, and show the "disabled until" toast. The toast cannot be
 * asserted on recent API levels, so a screenshot is written to the app's external files
 * dir (pull it with adb) for a visual check.
 */
@RunWith(AndroidJUnit4::class)
class MainActivityDisabledTest {

    private val app = ApplicationProvider.getApplicationContext<Context>() as OledSaverApplication
    private val settings = app.settingsRepository
    private val until = Instant.now().plus(Duration.ofMinutes(10)).truncatedTo(ChronoUnit.SECONDS)

    @Before
    fun seedDisabledWindow() = runBlocking {
        settings.setStartedDate(null)
        settings.setDisabledUntil(until)
    }

    @After
    fun cleanup() = runBlocking {
        settings.setDisabledUntil(null)
        settings.setStartedDate(null)
    }

    @Test
    fun launchDuringDisabledWindow_finishesWithoutStartingMasker() = runBlocking {
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        Thread.sleep(1_500) // let the coroutine decide and the toast appear
        saveScreenshot("disabled_toast.png")

        assertEquals(Lifecycle.State.DESTROYED, scenario.state)
        assertEquals("deadline must survive the launch", until, settings.disabledUntil.first())
        assertEquals("masker must not have been marked as started", Instant.EPOCH, settings.started.first())
    }

    private fun saveScreenshot(name: String) {
        val bitmap = InstrumentationRegistry.getInstrumentation().uiAutomation.takeScreenshot()
        val file = File(app.getExternalFilesDir(null), name)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 90, it) }
    }
}
