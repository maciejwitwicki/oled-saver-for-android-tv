package com.mwi.oledsaver.activity

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        OledSaverApplication.ALARM_MANAGER.initialize(applicationContext, alarmManager)

        if (MASK_APP_CONFIG.isEnabled()) {
            Log.i(LOGGING_TAG, "MainActivity - started")
            val intent = Intent(this, MaskerActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Log.i(LOGGING_TAG, "MainActivity - Out of operating hours, finishing")
            finishAndRemoveTask()
        }
    }
}
