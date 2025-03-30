package com.mwi.oledsaver

import android.app.Application
import com.mwi.oledsaver.alarm.MaskingAlarmManager
import com.mwi.oledsaver.config.ConfigProvider

class OledSaverApplication : Application() {

    companion object OledSaverApplication {
        const val LOGGING_TAG: String = "oled-saver"
        val MASK_APP_CONFIG: ConfigProvider = ConfigProvider()
        val ALARM_MANAGER = MaskingAlarmManager(MASK_APP_CONFIG)
    }

}
