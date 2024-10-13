package com.mwi.oledsaver.alarm

import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mwi.oledsaver.MainActivity.Companion.TAG
import com.mwi.oledsaver.activity.MaskerBroadcastReceiver
import java.time.Instant

class MaskingAlarmManager {

    private lateinit var alarmManager: AlarmManager

    fun initialize(context: Context?, alarmManager: AlarmManager) {
        this.alarmManager = alarmManager
        val startTimeMillis = Instant.now().toEpochMilli()
        this.execute(context, startTimeMillis)
    }

    fun execute(context: Context?, startTimeMillis: Long) {
        val intent = Intent(context, MaskerBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val windowDurationMillis = 10 * 60 * 1000
        Log.i(TAG, "scheduling Intent broadcast at window starting at %s".format(Instant.ofEpochMilli(startTimeMillis)))

        alarmManager.setWindow(RTC, startTimeMillis, windowDurationMillis.toLong(), pendingIntent)
    }

}
