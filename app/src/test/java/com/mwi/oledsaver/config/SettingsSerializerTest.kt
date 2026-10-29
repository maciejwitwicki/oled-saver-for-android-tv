package com.mwi.oledsaver.config

import androidx.datastore.core.CorruptionException
import com.google.protobuf.Timestamp
import com.mwi.oledsaver.config.SettingsProvider.SettingsSerializer
import com.mwi.oledsaver.proto.Settings
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/** Pure JVM: the proto serializer behind SettingsRepository, no DataStore or device needed. */
class SettingsSerializerTest {

    private val serializer = SettingsSerializer

    @Test
    fun defaultValue_isEmptySettings() {
        val d = serializer.defaultValue
        assertEquals(0L, d.started.seconds)
        assertEquals(0, d.maskIndex)
        assertFalse(d.hasDisabledUntil())
    }

    @Test
    fun writeThenRead_roundTripsAllFields() = runBlocking {
        val original = Settings.newBuilder()
            .setStarted(Timestamp.newBuilder().setSeconds(1_700_000_000))
            .setMaskIndex(2)
            .setDisabledUntil(Timestamp.newBuilder().setSeconds(1_700_000_600))
            .build()

        val bytes = ByteArrayOutputStream().also { serializer.writeTo(original, it) }.toByteArray()
        val restored = serializer.readFrom(ByteArrayInputStream(bytes))

        assertEquals(original, restored)
    }

    @Test
    fun emptyStream_readsAsDefault() = runBlocking {
        assertEquals(serializer.defaultValue, serializer.readFrom(ByteArrayInputStream(ByteArray(0))))
    }

    @Test
    fun garbage_isReportedAsCorruption() {
        // Field tag with wire type 7 (invalid) followed by junk: protobuf cannot parse it.
        val garbage = byteArrayOf(0x0F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x01, 0x02)
        assertThrows(CorruptionException::class.java) {
            runBlocking { serializer.readFrom(ByteArrayInputStream(garbage)) }
        }
    }
}
