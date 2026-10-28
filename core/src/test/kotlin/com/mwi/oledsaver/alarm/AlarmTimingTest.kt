package com.mwi.oledsaver.alarm

import com.mwi.oledsaver.config.ConfigProvider
import com.mwi.oledsaver.config.OperatingHours
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Clock
import java.time.ZonedDateTime

class AlarmTimingTest {

    private val zone = ConfigProvider.TIME_ZONE

    private fun at(hour: Int, minute: Int, day: Int = 27): ZonedDateTime =
        ZonedDateTime.of(2026, 10, day, hour, minute, 0, 0, zone)

    private fun configAt(now: ZonedDateTime) =
        ConfigProvider(OperatingHours.DEFAULT, Clock.fixed(now.toInstant(), zone))

    @Test
    fun minuteDelays_addRealMinutes() {
        val now = at(9, 0)
        assertEquals(at(9, 5).toInstant(), AlarmTiming.nextAlarmTime(AlarmDelay.DELAY_5_MIN, configAt(now)))
        assertEquals(at(10, 0).toInstant(), AlarmTiming.nextAlarmTime(AlarmDelay.DELAY_1_H, configAt(now)))
    }

    @Test
    fun nextDay_afterWindowStart_isTomorrowsStart() {
        assertEquals(at(7, 10, day = 28).toInstant(), AlarmTiming.nextAlarmTime(AlarmDelay.DELAY_NEXT_DAY, configAt(at(9, 0))))
    }

    @Test
    fun nextDay_beforeWindowStart_isTodaysStart() {
        assertEquals(at(7, 10).toInstant(), AlarmTiming.nextAlarmTime(AlarmDelay.DELAY_NEXT_DAY, configAt(at(6, 0))))
    }

    @Test
    fun nextDay_acrossDstEnd_keepsWallClockTime() {
        // 2026-10-24 is the day before DST ends in Warsaw: the next 07:10 is 25 real hours away.
        val now = ZonedDateTime.of(2026, 10, 24, 9, 0, 0, 0, zone)
        val next = AlarmTiming.nextAlarmTime(AlarmDelay.DELAY_NEXT_DAY, configAt(now))
        assertEquals(ZonedDateTime.of(2026, 10, 25, 7, 10, 0, 0, zone).toInstant(), next)
    }
}
