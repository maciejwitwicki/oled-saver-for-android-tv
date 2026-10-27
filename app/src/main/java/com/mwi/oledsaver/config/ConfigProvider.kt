package com.mwi.oledsaver.config

import android.util.Log
import android.util.Range
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ConfigProvider : ApplicationConfig {

    private val forceEnabled = false

    override fun isEnabled(logDebug: Boolean, started: Instant): Boolean {
        if (forceEnabled)
            return true

        if (wasStartedToday(started)) {
            return false;
        }

        val now = getNow()
        val start = getOperatingRange().lower
        val end = getOperatingRange().upper

        if (logDebug) {
            logDates(now, start, end)
        }
        return now.isAfter(start) && now.isBefore(end)
    }

    override fun decide(started: Instant, disabledUntil: Instant?): LaunchDecision {
        if (disabledUntil != null && getNow().toInstant().isBefore(disabledUntil)) {
            return LaunchDecision.Disabled(disabledUntil)
        }
        return if (isEnabled(started = started)) LaunchDecision.Start else LaunchDecision.Skip
    }

    private fun logDates(now: ZonedDateTime, start: ZonedDateTime, end: ZonedDateTime) {
        val nowFormatted = now.format(TIME_FORMAT)
        val startFormatted = start.format(TIME_FORMAT)
        val endFormatted = end.format(TIME_FORMAT)
        Log.i(
            LOGGING_TAG,
            """
                ConfigProvider#isEnabled
                  - now: $nowFormatted
                  - start: $startFormatted
                  - end: $endFormatted
            """.trimIndent()
        )
    }

    override fun getOperatingRange(): Range<ZonedDateTime> {
        val start = getNow().withHour(7)
            .withMinute(10).withSecond(0)
        val end = getNow().withHour(12)
            .withMinute(0).withSecond(0)

        return Range(start, end)
    }

    override fun getNow(): ZonedDateTime {
        return ZonedDateTime.now(TIME_ZONE)
    }

    private fun wasStartedToday(started: Instant): Boolean {
        val ld1 = LocalDateTime.ofInstant(started, ZoneId.systemDefault()).toLocalDate().dayOfMonth
        val ld2 = LocalDateTime.now().dayOfMonth
        return ld1 == ld2
    }

    companion object {
        val TIME_ZONE: ZoneId = ZoneId.of("Europe/Warsaw")
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        private val TIME_ONLY: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private val DAY_AND_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

        /** "07:10" if [instant] falls on today's date in [TIME_ZONE], otherwise "28.10 07:10". */
        fun formatForUser(instant: Instant): String {
            val zoned = instant.atZone(TIME_ZONE)
            val sameDay = zoned.toLocalDate() == ZonedDateTime.now(TIME_ZONE).toLocalDate()
            return zoned.format(if (sameDay) TIME_ONLY else DAY_AND_TIME)
        }
    }

}
