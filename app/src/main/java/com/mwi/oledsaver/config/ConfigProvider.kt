package com.mwi.oledsaver.config

import android.util.Log
import android.util.Range
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class ConfigProvider : ApplicationConfig {

    private val forceEnabled = true

    override fun isEnabled(logDebug: Boolean): Boolean {
        if (forceEnabled)
            return true

        val now = getNow()
        val start = getOperatingRange().lower
        val end = getOperatingRange().upper

        if (logDebug) {
            logDates(now, start, end)
        }
        return now.isAfter(start) && now.isBefore(end)
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
            .withMinute(45).withSecond(0)
        val end = getNow().withHour(12)
            .withMinute(0).withSecond(0)

        return Range(start, end)
    }

    override fun getNow(): ZonedDateTime {
        return ZonedDateTime.now(TIME_ZONE)
    }

    companion object {
        val TIME_ZONE: ZoneId = ZoneId.of("Europe/Warsaw")
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }

}
