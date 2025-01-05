package com.mwi.oledsaver.activity

import android.app.AlarmManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(LOGGING_TAG, "BootBroadcastReceiver - Received broadcast intent")

        val i = Intent(context, MainActivity::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context!!.startActivity(i)
    }
}
