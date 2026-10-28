package com.mwi.oledsaver.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.LocalTime
import java.time.ZonedDateTime

class ConfigProviderTest {

    private val zone = ConfigProvider.TIME_ZONE
    private val notStarted: Instant = Instant.EPOCH

    private fun at(hour: Int, minute: Int, day: Int = 27): ZonedDateTime =
        ZonedDateTime.of(2026, 10, day, hour, minute, 0, 0, zone)

    private fun providerAt(now: ZonedDateTime, hours: OperatingHours = OperatingHours.DEFAULT) =
        ConfigProvider(hours, Clock.fixed(now.toInstant(), zone))

    @Test
    fun insideWindow_andNotStarted_starts() {
        assertEquals(LaunchDecision.Start, providerAt(at(8, 0)).decide(notStarted, null))
    }

    @Test
    fun outsideWindow_skips() {
        assertEquals(LaunchDecision.Skip, providerAt(at(6, 59)).decide(notStarted, null))
        assertEquals(LaunchDecision.Skip, providerAt(at(12, 1)).decide(notStarted, null))
    }

    @Test
    fun windowBoundsAreExclusive() {
        assertFalse(providerAt(at(7, 10)).isEnabled(started = notStarted))
        assertTrue(providerAt(at(7, 11)).isEnabled(started = notStarted))
    }

    @Test
    fun startedEarlierToday_skips() {
        val now = at(9, 0)
        assertEquals(LaunchDecision.Skip, providerAt(now).decide(at(7, 15).toInstant(), null))
    }

    @Test
    fun startedYesterday_doesNotCountAsToday() {
        val now = at(9, 0)
        assertEquals(LaunchDecision.Start, providerAt(now).decide(at(9, 0, day = 26).toInstant(), null))
    }

    @Test
    fun sameDayOfMonthInAnotherMonth_doesNotCountAsToday() {
        // Regression: the old check compared dayOfMonth only.
        val now = at(9, 0)
        val lastMonth = ZonedDateTime.of(2026, 9, 27, 9, 0, 0, 0, zone).toInstant()
        assertEquals(LaunchDecision.Start, providerAt(now).decide(lastMonth, null))
    }

    @Test
    fun disabledUntilInFuture_winsOverEverything() {
        val now = at(9, 0)
        val until = now.plusMinutes(10).toInstant()
        val decision = providerAt(now).decide(started = now.toInstant(), disabledUntil = until)
        assertEquals(LaunchDecision.Disabled(until), decision)
    }

    @Test
    fun expiredDisabledUntil_isIgnored() {
        val now = at(9, 0)
        assertEquals(LaunchDecision.Start, providerAt(now).decide(notStarted, now.minusMinutes(1).toInstant()))
    }

    @Test
    fun aroundNow_overrideAlwaysStartsOutsideRealHours() {
        assertEquals(LaunchDecision.Start, providerAt(at(23, 30), OperatingHours.AroundNow()).decide(notStarted, null))
    }

    @Test
    fun operatingWindow_isBuiltForToday() {
        val window = providerAt(at(15, 0)).getOperatingWindow()
        assertEquals(at(7, 10), window.start)
        assertEquals(at(12, 0), window.end)
    }

    @Test
    fun formatForUser_showsTimeOnlyForSameDay() {
        val now = at(9, 0)
        assertEquals("09:05", ConfigProvider.formatForUser(now.plusMinutes(5).toInstant(), now))
        assertEquals("28.10 07:10", ConfigProvider.formatForUser(at(7, 10, day = 28).toInstant(), now))
    }

    @Test
    fun customFixedHours_areHonoured() {
        val evening = OperatingHours.Fixed(LocalTime.of(19, 0), LocalTime.of(21, 0))
        assertEquals(LaunchDecision.Start, providerAt(at(20, 0), evening).decide(notStarted, null))
        assertEquals(LaunchDecision.Skip, providerAt(at(8, 0), evening).decide(notStarted, null))
    }

    @Test
    fun snoozeOfOneHour_isSixtyRealMinutesAcrossDstChange() {
        // DST ended 2026-10-25 03:00 in Warsaw; 02:30 CEST + 60 real minutes = 02:30 CET.
        val beforeShift = ZonedDateTime.of(2026, 10, 25, 2, 30, 0, 0, zone)
        val later = beforeShift.plusMinutes(60)
        assertEquals(Duration.ofHours(1), Duration.between(beforeShift.toInstant(), later.toInstant()))
    }
}
