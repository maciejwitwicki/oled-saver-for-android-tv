package com.mwi.oledsaver.alarm

import com.mwi.oledsaver.config.ApplicationConfig
import com.mwi.oledsaver.config.TimeWindow
import java.time.Instant
import java.time.ZonedDateTime

/** Pure computation of when the next launch alarm should fire. */
object AlarmTiming {

    fun nextAlarmTime(delay: AlarmDelay, config: ApplicationConfig): Instant {
        val now = config.getNow()
        val minutes = delay.minutes ?: return nextWindowStart(now, config.getOperatingWindow())
        // plusMinutes on ZonedDateTime works on the instant time-line: a 60-minute snooze is
        // always 60 real minutes, also across a DST change.
        return now.plusMinutes(minutes).toInstant()
    }

    /** Today's window start if still ahead, otherwise the same wall-clock time tomorrow. */
    fun nextWindowStart(now: ZonedDateTime, window: TimeWindow): Instant =
        if (now.isBefore(window.start)) window.start.toInstant()
        else window.start.plusDays(1).toInstant()
}
