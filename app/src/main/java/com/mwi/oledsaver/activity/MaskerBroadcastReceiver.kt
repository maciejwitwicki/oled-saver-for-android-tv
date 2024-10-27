package com.mwi.oledsaver.activity

import android.app.ActivityOptions
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityOptionsCompat
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


        val activityOptions = ActivityOptions.makeBasic()
        // will be required in android 34
        //activityOptions.pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
        //activityOptions.pendingIntentCreatorBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED

        context?.startActivity(restoreActivityIntent, activityOptions.toBundle())
    }
}
