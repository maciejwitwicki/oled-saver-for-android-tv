package com.mwi.oledsaver.exit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.FragmentActivity
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.alarm.AlarmDelay
import com.mwi.oledsaver.alarm.MaskingAlarmManager

class ExitActivity : FragmentActivity() {

    private val maskingAlarmManager: MaskingAlarmManager = OledSaverApplication.ALARM_MANAGER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - create")
        maskingAlarmManager.cancelNextExecution()

        setContentView(R.layout.activity_exit)

        findViewById<View>(R.id.exit_button_dismiss_5m)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_5_MIN)
            }
        findViewById<View>(R.id.exit_button_dismiss_10m)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_10_MIN)
            }
        findViewById<View>(R.id.exit_button_dismiss_1h)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_1_H)
            }
        findViewById<View>(R.id.exit_button_dismiss_1d)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_NEXT_DAY)
            }

            this.onBackPressedDispatcher
            .addCallback(this) {
                Log.i(LOGGING_TAG, "${this.javaClass.simpleName} Back pressed!")
                dismiss(AlarmDelay.DELAY_5_MIN)
                finish()
            }
    }


    private fun dismiss(delay: AlarmDelay) {
        Toast.makeText(this, "Odkladanie na $delay", Toast.LENGTH_SHORT).show()
        maskingAlarmManager.scheduleNextAlarmExecution(delay)
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - restart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - resume")
    }

    override fun onPause() {
        super.onPause()
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - pause")

    }

    override fun onStart() {
        super.onStart()
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - start")
    }

    override fun onStop() {
        super.onStop()
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - stop")
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - activityReenter")

    }
}
