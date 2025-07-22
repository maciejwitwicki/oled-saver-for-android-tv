package com.mwi.oledsaver.activity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i(LOGGING_TAG, "BootBroadcastReceiver - Received broadcast intent")
        if (intent != null && (intent.action == "BOOT_COMPLETED" || intent.action == "QUICKBOOT_POWERON")) {
            val i = Intent(context, MainActivity::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context!!.startActivity(i)
        }
    }
}
