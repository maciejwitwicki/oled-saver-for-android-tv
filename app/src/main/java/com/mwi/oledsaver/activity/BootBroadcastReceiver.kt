package com.mwi.oledsaver.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Relaunches MainActivity after a reboot. Alarms do not survive a restart, so this is the
 * only in-app path that brings masking back without a manual or external launch.
 */
class BootBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        Log.i(LOGGING_TAG, "BootBroadcastReceiver - Received broadcast intent: $action")
        if (context == null || action !in BOOT_ACTIONS) return

        val settings = (context.applicationContext as OledSaverApplication).settingsRepository
        // Keep the receiver alive while the DataStore write runs off the main thread.
        // goAsync() is null when onReceive() is invoked directly (e.g. from a test).
        val pendingResult: PendingResult? = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // A reboot while the masker was showing leaves "started today" set, which
                // would make MainActivity skip for the rest of the day. The deadline from a
                // dismissal (disabled_until) is intentionally kept.
                settings.setStartedDate(null)
            } finally {
                val launch = Intent(context, MainActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launch)
                pendingResult?.finish()
            }
        }
    }

    companion object {
        private val BOOT_ACTIONS = setOf(
            Intent.ACTION_BOOT_COMPLETED,
            "android.intent.action.QUICKBOOT_POWERON"
        )
    }
}
