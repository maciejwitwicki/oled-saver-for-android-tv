package com.mwi.oledsaver.activity

import android.app.ActivityOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG

class MaskerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(LOGGING_TAG, "MaskerBroadcastReceiver - Received broadcast intent")

        //OledSaverApplication.ALARM_MANAGER.scheduleNextExecution(context)
        val restoreActivityIntent = Intent(context, MaskerActivity::class.java)
        restoreActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val activityOptions = ActivityOptions.makeBasic()
        // will be required in android 34
        //activityOptions.pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
        //activityOptions.pendingIntentCreatorBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED

        context?.startActivity(restoreActivityIntent, activityOptions.toBundle())
    }
}
