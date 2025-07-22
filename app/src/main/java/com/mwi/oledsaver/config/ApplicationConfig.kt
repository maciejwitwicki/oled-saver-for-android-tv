package com.mwi.oledsaver.config

import android.util.Range
import java.time.ZonedDateTime

interface ApplicationConfig {

    fun isEnabled(logDebug: Boolean = false): Boolean
    fun getOperatingRange(): Range<ZonedDateTime>
    fun getNow(): ZonedDateTime
}
