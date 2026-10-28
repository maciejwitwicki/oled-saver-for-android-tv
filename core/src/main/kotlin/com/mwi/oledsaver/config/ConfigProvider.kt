package com.mwi.oledsaver.config

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

/**
 * Decides whether masking may start. Pure JVM: time comes from [clock], diagnostics go
 * to [log], so the class is fully testable and has no Android dependency.
 */
class ConfigProvider(
    private val operatingHours: OperatingHours = OperatingHours.DEFAULT,
    private val clock: Clock = Clock.system(TIME_ZONE),
    private val log: (String) -> Unit = {}
) : ApplicationConfig {

    private val forceEnabled = false

    init {
        log("ConfigProvider - operating hours: $operatingHours")
    }

    override fun isEnabled(logDebug: Boolean, started: Instant): Boolean {
        if (forceEnabled)
            return true

        if (wasStartedToday(started)) {
            return false
        }

        val now = getNow()
        val window = getOperatingWindow()

        if (logDebug) {
            logDates(now, window)
        }
        return now in window
    }

    override fun decide(started: Instant, disabledUntil: Instant?): LaunchDecision {
        if (disabledUntil != null && getNow().toInstant().isBefore(disabledUntil)) {
            return LaunchDecision.Disabled(disabledUntil)
        }
        return if (isEnabled(started = started)) LaunchDecision.Start else LaunchDecision.Skip
    }

    override fun getOperatingWindow(): TimeWindow {
        val (start, end) = operatingHours.boundsAt(getNow())
        return TimeWindow(start, end)
    }

    override fun getNow(): ZonedDateTime = ZonedDateTime.now(clock)

    /** Same calendar date in [TIME_ZONE]; the epoch value used for "not started" never matches. */
    private fun wasStartedToday(started: Instant): Boolean =
        started.atZone(TIME_ZONE).toLocalDate() == getNow().toLocalDate()

    private fun logDates(now: ZonedDateTime, window: TimeWindow) {
        log(
            """
                ConfigProvider#isEnabled
                  - now: ${now.format(TIME_FORMAT)}
                  - start: ${window.start.format(TIME_FORMAT)}
                  - end: ${window.end.format(TIME_FORMAT)}
            """.trimIndent()
        )
    }

    companion object {
        val TIME_ZONE: ZoneId = ZoneId.of("Europe/Warsaw")
        val TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        private val TIME_ONLY: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        private val DAY_AND_TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM HH:mm")

        /** "07:10" if [instant] falls on the date of [now] in [TIME_ZONE], otherwise "28.10 07:10". */
        fun formatForUser(instant: Instant, now: ZonedDateTime = ZonedDateTime.now(TIME_ZONE)): String {
            val zoned = instant.atZone(TIME_ZONE)
            val sameDay = zoned.toLocalDate() == now.withZoneSameInstant(TIME_ZONE).toLocalDate()
            return zoned.format(if (sameDay) TIME_ONLY else DAY_AND_TIME)
        }
    }
}
