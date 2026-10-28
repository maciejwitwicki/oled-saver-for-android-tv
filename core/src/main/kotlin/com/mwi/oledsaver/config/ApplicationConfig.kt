package com.mwi.oledsaver.config

import java.time.Instant
import java.time.ZonedDateTime

/** Outcome of the launch check performed by the app entry point. */
sealed class LaunchDecision {
    /** Masking should start now. */
    data object Start : LaunchDecision()

    /** The user dismissed masking; stay off until [until]. */
    data class Disabled(val until: Instant) : LaunchDecision()

    /** Outside the operating window, or an instance already started today. */
    data object Skip : LaunchDecision()
}

/** A concrete start/end pair of the operating window on a given day. */
data class TimeWindow(val start: ZonedDateTime, val end: ZonedDateTime) {
    operator fun contains(moment: ZonedDateTime): Boolean =
        moment.isAfter(start) && moment.isBefore(end)
}

interface ApplicationConfig {

    fun isEnabled(logDebug: Boolean = false, started: Instant): Boolean
    fun decide(started: Instant, disabledUntil: Instant?): LaunchDecision
    fun getOperatingWindow(): TimeWindow
    fun getNow(): ZonedDateTime
}
