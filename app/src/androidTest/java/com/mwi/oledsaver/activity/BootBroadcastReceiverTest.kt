package com.mwi.oledsaver.activity

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mwi.oledsaver.OledSaverApplication
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Duration
import java.time.Instant

@RunWith(AndroidJUnit4::class)
class BootBroadcastReceiverTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val settings = (context as OledSaverApplication).settingsRepository
    private val startedEarlierToday: Instant = Instant.now().minus(Duration.ofMinutes(30))

    @Before
    fun seedStaleStartedFlag() = runBlocking {
        settings.setStartedDate(startedEarlierToday)
    }

    @After
    fun cleanup() = runBlocking {
        settings.setStartedDate(null)
        settings.setDisabledUntil(null)
    }

    @Test
    fun bootCompleted_clearsStaleStartedFlag() = runBlocking {
        BootBroadcastReceiver().onReceive(context, Intent(Intent.ACTION_BOOT_COMPLETED))

        withTimeout(5_000) {
            while (settings.started.first() != Instant.EPOCH) delay(50)
        }
        assertEquals(Instant.EPOCH, settings.started.first())
    }

    @Test
    fun quickBootPowerOn_isAcceptedToo() = runBlocking {
        BootBroadcastReceiver().onReceive(context, Intent("android.intent.action.QUICKBOOT_POWERON"))

        withTimeout(5_000) {
            while (settings.started.first() != Instant.EPOCH) delay(50)
        }
        assertEquals(Instant.EPOCH, settings.started.first())
    }

    @Test
    fun unrelatedAction_isIgnored() = runBlocking {
        BootBroadcastReceiver().onReceive(context, Intent(Intent.ACTION_TIMEZONE_CHANGED))

        delay(1_000)
        assertEquals(
            "started flag must be untouched",
            startedEarlierToday.epochSecond, settings.started.first().epochSecond
        )
    }
}
