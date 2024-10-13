package com.mwi.oledsaver.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mwi.oledsaver.MainActivity.Companion.TAG
import com.mwi.oledsaver.OledSaverApplication
import java.time.Duration
import java.time.Instant

class MaskerBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(TAG, "received broadcast intent")
        val nextOccurrence = Instant.now().plus(Duration.ofMinutes(10)).toEpochMilli()
        Log.i(TAG, "scheduling next intent launch in %s millis".format(nextOccurrence))

        OledSaverApplication.ALARM_MANAGER.execute(context, nextOccurrence)
        val restoreActivityIntent = Intent(context, RestoreMaskerActivity::class.java)
        restoreActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context?.startActivity(intent)
    }
}
