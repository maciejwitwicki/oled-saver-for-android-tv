package com.mwi.oledsaver.config

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStoreFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.config.SettingsProvider.SettingsSerializer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * SettingsRepository against the real DataStore file. Uses the app's single repository
 * instance because DataStore forbids two instances on the same file.
 */
@RunWith(AndroidJUnit4::class)
class SettingsRepositoryTest {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val settings = (context as OledSaverApplication).settingsRepository

    // Second precision: the proto stores epoch seconds.
    private val t1: Instant = Instant.now().truncatedTo(ChronoUnit.SECONDS)
    private val t2: Instant = t1.plus(Duration.ofMinutes(10))

    @Before
    fun reset() = runBlocking { clearAll() }

    @After
    fun cleanup() = runBlocking { clearAll() }

    private suspend fun clearAll() {
        settings.setStartedDate(null)
        settings.setDisabledUntil(null)
        settings.setMaskIndex(0)
    }

    @Test
    fun startedDate_roundTrips_andNullReadsAsEpoch() = runBlocking {
        settings.setStartedDate(t1)
        assertEquals(t1, settings.started.first())

        settings.setStartedDate(null)
        assertEquals(Instant.EPOCH, settings.started.first())
    }

    @Test
    fun startedDate_dropsSubSecondPrecision() = runBlocking {
        val withNanos = t1.plusNanos(987_654_321)
        settings.setStartedDate(withNanos)
        assertEquals(t1, settings.started.first())
    }

    @Test
    fun disabledUntil_roundTrips_andNullReadsAsNull() = runBlocking {
        settings.setDisabledUntil(t2)
        assertEquals(t2, settings.disabledUntil.first())

        settings.setDisabledUntil(null)
        assertNull(settings.disabledUntil.first())
    }

    @Test
    fun maskIndex_roundTrips_includingZero() = runBlocking {
        settings.setMaskIndex(2)
        assertEquals(2, settings.maskIndex.first())

        settings.setMaskIndex(0)
        assertEquals(0, settings.maskIndex.first())
    }

    @Test
    fun writingOneField_leavesTheOthersIntact() = runBlocking {
        settings.setStartedDate(t1)
        settings.setDisabledUntil(t2)
        settings.setMaskIndex(1)

        settings.setMaskIndex(2)
        assertEquals(t1, settings.started.first())
        assertEquals(t2, settings.disabledUntil.first())

        settings.setStartedDate(null)
        assertEquals(t2, settings.disabledUntil.first())
        assertEquals(2, settings.maskIndex.first())
    }

    @Test
    fun corruptedFile_failsReadWithCorruptionException() {
        // Separate file and DataStore instance so the app's own store is not touched.
        val file = File(context.cacheDir, "settings-corrupt-test.pb")
        file.writeBytes(byteArrayOf(0x0F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x01, 0x02))
        val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
        val store = DataStoreFactory.create(serializer = SettingsSerializer, scope = scope) { file }

        try {
            assertThrows(CorruptionException::class.java) {
                runBlocking { store.data.first() }
            }
        } finally {
            scope.cancel()
            file.delete()
        }
    }
}
