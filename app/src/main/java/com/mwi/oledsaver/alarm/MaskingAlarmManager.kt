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
import java.time.ZonedDateTime
import kotlin.time.Duration

class MaskingAlarmManager(private val configProvider: ConfigProvider) {

    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent

    fun initialize(context: Context?, alarmManager: AlarmManager) {
        this.alarmManager = alarmManager
        this.pendingIntent = setPendingIntent(context)
        this.scheduleNextExecution()
    }

    fun scheduleNextExecution() {
        alarmManager.cancel(pendingIntent)
        scheduleNextAlarmExecution(AlarmDelay.DELAY_1_MIN)
    }

    fun cancelNextExecution() {
        Log.i(LOGGING_TAG, "Canceling next masking alarm execution")
        alarmManager.cancel(pendingIntent)
    }

    fun scheduleNextAlarmExecution(delay: AlarmDelay) {
        val alarmTimeMillis = getNextAlarmTimeMillis(delay)
        val alarmDate = Instant.ofEpochMilli(alarmTimeMillis)
            .atZone(ConfigProvider.TIME_ZONE)
            .format(ConfigProvider.TIME_FORMAT)
        Log.i(LOGGING_TAG, "scheduling Intent broadcast for $delay at $alarmDate")
        alarmManager.set(RTC, alarmTimeMillis, pendingIntent)
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

    private fun getNextAlarmTimeMillis(delay: AlarmDelay): Long {
        if (delay == AlarmDelay.DELAY_NEXT_DAY || !configProvider.isEnabled()) {
            return getNextDayExecutionTimeMillis()
        } else {
            return configProvider.getNow()
                .plusMinutes(toMinutes(delay))
                .toInstant()
                .toEpochMilli()
        }
    }

    private fun toMinutes(delay: AlarmDelay): Long {
        return when(delay) {
            AlarmDelay.DELAY_1_MIN -> 1
            AlarmDelay.DELAY_5_MIN -> 5
            AlarmDelay.DELAY_10_MIN -> 10
            AlarmDelay.DELAY_1_H -> 60
            AlarmDelay.DELAY_NEXT_DAY -> throw IllegalArgumentException("Next Day can't be parsed to minutes")
        }
    }

    private fun getNextDayExecutionTimeMillis(): Long {
        val now = configProvider.getNow()
        val operatingRange = configProvider.getOperatingRange()
        if (now.isBefore(operatingRange.lower)) {
            return operatingRange.lower.toInstant().toEpochMilli()
        }
        return operatingRange.lower.plusDays(1).toInstant().toEpochMilli()
    }

}
