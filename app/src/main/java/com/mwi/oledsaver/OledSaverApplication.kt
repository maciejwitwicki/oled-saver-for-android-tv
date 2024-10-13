package com.mwi.oledsaver

import android.app.Application
import com.mwi.oledsaver.alarm.MaskingAlarmManager

class OledSaverApplication : Application() {

    companion object OledSaverApplication {
        val ALARM_MANAGER = MaskingAlarmManager()
    }

}
