package com.mwi.oledsaver.alarm

import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.activity.MaskerBroadcastReceiver
import com.mwi.oledsaver.config.ConfigProvider
import java.time.Instant

class MaskingAlarmManager(private val configProvider: ConfigProvider) {

    private val name = this.javaClass.simpleName
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    /** Prepares the alarm manager and pending intent. Does not schedule anything. */
    fun initialize(context: Context?) {
        this.pendingIntent = setPendingIntent(context)
        getAlarmManager(context)
        Log.i(LOGGING_TAG, "[$name] initialize")
    }

    /** Re-arms the regular one-minute heartbeat. */
    fun scheduleNextExecution() {
        scheduleNextAlarmExecution(AlarmDelay.DELAY_1_MIN)
    }

    fun cancelNextExecution() {
        Log.i(LOGGING_TAG, "Canceling next masking alarm execution")
        alarmManager.cancel(pendingIntent)
    }

    /** Schedules the next launch after [delay] and returns the instant it was scheduled for. */
    fun scheduleNextAlarmExecution(delay: AlarmDelay): Instant {
        val alarmTime = AlarmTiming.nextAlarmTime(delay, configProvider)
        scheduleAt(alarmTime, "for $delay")
        return alarmTime
    }

    /** Replaces any pending alarm with one firing at [instant]. */
    fun scheduleAt(instant: Instant, reason: String = "") {
        cancelNextExecution()
        val alarmDate = instant.atZone(ConfigProvider.TIME_ZONE).format(ConfigProvider.TIME_FORMAT)
        Log.i(LOGGING_TAG, "scheduling Intent broadcast $reason at $alarmDate")
        alarmManager.set(RTC, instant.toEpochMilli(), pendingIntent)
    }

    private fun getAlarmManager(context: Context?): AlarmManager {
        this.alarmManager = context?.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return this.alarmManager
    }

    private fun setPendingIntent(context: Context?): PendingIntent {
        val intent = Intent(context, MaskerBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE +
                    PendingIntent.FLAG_UPDATE_CURRENT
        )
        this.pendingIntent = pendingIntent
        return pendingIntent!!
    }

}
