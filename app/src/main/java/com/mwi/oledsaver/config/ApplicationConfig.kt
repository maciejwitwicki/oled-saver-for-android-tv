package com.mwi.oledsaver.config

import android.util.Range
import java.time.Instant
import java.time.ZonedDateTime

/** Outcome of the launch check performed by MainActivity. */
sealed class LaunchDecision {
    /** Masking should start now. */
    data object Start : LaunchDecision()

    /** The user dismissed masking; stay off until [until]. */
    data class Disabled(val until: Instant) : LaunchDecision()

    /** Outside the operating window, or an instance already started today. */
    data object Skip : LaunchDecision()
}

interface ApplicationConfig {

    fun isEnabled(logDebug: Boolean = false, started: Instant): Boolean
    fun decide(started: Instant, disabledUntil: Instant?): LaunchDecision
    fun getOperatingRange(): Range<ZonedDateTime>
    fun getNow(): ZonedDateTime
}
