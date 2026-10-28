package com.mwi.oledsaver.config

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class OperatingHoursTest {

    private val warsaw = ZoneId.of("Europe/Warsaw")
    private val now = ZonedDateTime.of(2026, 10, 27, 15, 30, 42, 123, warsaw)

    @Test
    fun default_isSevenTenToNoon() {
        assertEquals(OperatingHours.Fixed(LocalTime.of(7, 10), LocalTime.of(12, 0)), OperatingHours.DEFAULT)
    }

    @Test
    fun fixed_boundsAreOnTheSameDayWithWholeSeconds() {
        val (start, end) = OperatingHours.DEFAULT.boundsAt(now)

        assertEquals(ZonedDateTime.of(2026, 10, 27, 7, 10, 0, 0, warsaw), start)
        assertEquals(ZonedDateTime.of(2026, 10, 27, 12, 0, 0, 0, warsaw), end)
    }

    @Test
    fun aroundNow_alwaysContainsNow() {
        val (start, end) = OperatingHours.AroundNow().boundsAt(now)

        assertTrue(start.isBefore(now))
        assertTrue(end.isAfter(now))
    }

    @Test
    fun parse_acceptsNowAndRanges() {
        assertTrue(OperatingHours.parse("now") is OperatingHours.AroundNow)
        assertTrue(OperatingHours.parse(" NOW ") is OperatingHours.AroundNow)
        assertEquals(
            OperatingHours.Fixed(LocalTime.of(14, 0), LocalTime.of(18, 30)),
            OperatingHours.parse("14:00-18:30")
        )
    }

    @Test
    fun parse_rejectsBlankAndMalformed() {
        assertNull(OperatingHours.parse(null))
        assertNull(OperatingHours.parse(""))
        assertNull(OperatingHours.parse("14:00"))
        assertNull(OperatingHours.parse("14:00-25:99"))
        assertNull(OperatingHours.parse("morning"))
    }
}
