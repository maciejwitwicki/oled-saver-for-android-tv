package com.mwi.oledsaver.alarm

import android.app.AlarmManager
import android.app.AlarmManager.RTC
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import com.mwi.oledsaver.activity.MaskerBroadcastReceiver
import com.mwi.oledsaver.config.ConfigProvider
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

class MaskingAlarmManager(private val configProvider: ConfigProvider) {

    private val MaskerRefreshPeriodMinutes = 1L
    private lateinit var alarmManager: AlarmManager

    fun initialize(context: Context?, alarmManager: AlarmManager) {
        this.alarmManager = alarmManager
        this.execute(context)
    }

    fun execute(context: Context?) {
        val intent = Intent(context, MaskerBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context, 0, intent,
            PendingIntent.FLAG_MUTABLE +
                    PendingIntent.FLAG_UPDATE_CURRENT
        )

        val alarmTimeMillis = getNextAlarmTimeMillis()
        val alarmDate = Instant.ofEpochMilli(alarmTimeMillis)
            .atZone(ConfigProvider.TIME_ZONE)
            .format(ConfigProvider.TIME_FORMAT)
        Log.i(LOGGING_TAG, "scheduling Intent broadcast at $alarmDate")

        alarmManager.set(RTC, alarmTimeMillis, pendingIntent)
    }

    private fun getNextAlarmTimeMillis(): Long {
        val now = configProvider.getNow()

        if (configProvider.isEnabled()) {
            return now.plusMinutes(MaskerRefreshPeriodMinutes).toInstant().toEpochMilli()
        } else {
            val operatingRange = configProvider.getOperatingRange()
            if (now.isBefore(operatingRange.lower)) {
                return operatingRange.lower.toInstant().toEpochMilli()
            }
            return operatingRange.lower.plusDays(1).toInstant().toEpochMilli()
        }
    }

}
