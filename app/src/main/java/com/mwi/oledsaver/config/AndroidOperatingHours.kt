package com.mwi.oledsaver.config

import android.os.Build
import android.util.Log
import com.mwi.oledsaver.BuildConfig
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG

/** Android adapter: picks the [OperatingHours] for this process from build config and device. */
object AndroidOperatingHours {

    /**
     * Debug builds running on an emulator may override [OperatingHours.DEFAULT] through
     * `oledsaver.operatingHours` in `local.properties`; release builds and real devices
     * always get the default.
     */
    fun fromEnvironment(): OperatingHours {
        val override = BuildConfig.OPERATING_HOURS_OVERRIDE
        if (!BuildConfig.DEBUG || override.isBlank()) return OperatingHours.DEFAULT
        if (!isEmulator()) {
            Log.w(LOGGING_TAG, "OperatingHours - override '$override' ignored on a real device")
            return OperatingHours.DEFAULT
        }
        val parsed = OperatingHours.parse(override)
        if (parsed == null) {
            Log.w(LOGGING_TAG, "OperatingHours - cannot parse override '$override', using default")
            return OperatingHours.DEFAULT
        }
        return parsed
    }

    fun isEmulator(): Boolean =
        Build.HARDWARE in setOf("ranchu", "goldfish", "cutf_cvm") ||
                Build.FINGERPRINT.startsWith("generic") ||
                Build.PRODUCT.startsWith("sdk_") ||
                Build.PRODUCT.contains("emulator")
}
