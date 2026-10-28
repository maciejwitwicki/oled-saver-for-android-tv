package com.mwi.oledsaver.config

import java.time.Duration
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/** The daily window during which masking is allowed to start. */
sealed interface OperatingHours {

    /** Start and end of the window on the day of [now], in the zone of [now]. */
    fun boundsAt(now: ZonedDateTime): Pair<ZonedDateTime, ZonedDateTime>

    /** Production behaviour: a fixed wall-clock window every day. */
    data class Fixed(val start: LocalTime, val end: LocalTime) : OperatingHours {
        override fun boundsAt(now: ZonedDateTime) = now.with(start) to now.with(end)
        override fun toString() = "${start.format(TIME)}-${end.format(TIME)}"
    }

    /** Testing aid: a window that always contains the current time. */
    data class AroundNow(
        val before: Duration = Duration.ofMinutes(5),
        val after: Duration = Duration.ofHours(3)
    ) : OperatingHours {
        override fun boundsAt(now: ZonedDateTime) = now.minus(before) to now.plus(after)
        override fun toString() = "now (-${before.toMinutes()}m / +${after.toMinutes()}m)"
    }

    companion object {
        val DEFAULT: OperatingHours = Fixed(LocalTime.of(7, 10), LocalTime.of(12, 0))
        private val TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        /** Parses `"now"` or `"HH:mm-HH:mm"`. Returns null for blank or malformed input. */
        fun parse(spec: String?): OperatingHours? {
            val text = spec?.trim().orEmpty()
            if (text.isEmpty()) return null
            if (text.equals("now", ignoreCase = true)) return AroundNow()
            val parts = text.split('-')
            if (parts.size != 2) return null
            return try {
                Fixed(LocalTime.parse(parts[0].trim()), LocalTime.parse(parts[1].trim()))
            } catch (e: DateTimeParseException) {
                null
            }
        }
    }
}
