package com.mwi.oledsaver.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.util.Log
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import java.time.Duration
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit

class MaskerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(LOGGING_TAG, "MaskerBroadcastReceiver - Received broadcast intent")
        OledSaverApplication.ALARM_MANAGER.execute(context)
        val restoreActivityIntent = Intent(context, MaskerActivity::class.java)
        restoreActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(restoreActivityIntent)
    }
}
