package com.mwi.oledsaver.activity

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.config.ConfigProvider
import com.mwi.oledsaver.config.LaunchDecision
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Instant


class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleLaunch()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        Log.i(LOGGING_TAG, "MainActivity - onNewIntent called")
        handleLaunch()
    }

    private fun handleLaunch() {

        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )

        val alarmManager = OledSaverApplication.ALARM_MANAGER
        alarmManager.initialize(applicationContext)

        val canDrawOverlays = Settings.canDrawOverlays(this)
        Log.i(LOGGING_TAG, "Can draw overlays $canDrawOverlays")

        if (!canDrawOverlays) {
            val askForPermission = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(askForPermission)
        }

        val settings = (application as OledSaverApplication).settingsRepository
        val intent = Intent(this, MaskerActivity::class.java)

        lifecycleScope.launch {
            // One-shot reads: collecting here would re-enter this block after setStartedDate().
            val started = settings.started.first()
            val disabledUntil = settings.disabledUntil.first()

            when (val decision = MASK_APP_CONFIG.decide(started, disabledUntil)) {
                is LaunchDecision.Start -> {
                    Log.i(LOGGING_TAG, "MainActivity - conditions met, starting MaskerActivity")
                    alarmManager.scheduleNextExecution()
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    settings.setStartedDate(Instant.now())
                    finish()
                }

                is LaunchDecision.Disabled -> {
                    val untilText = ConfigProvider.formatForUser(decision.until)
                    Log.i(LOGGING_TAG, "MainActivity - disabled until $untilText, finishing")
                    // Wake up when the dismissal ends instead of polling every minute,
                    // otherwise this toast would reappear on every heartbeat.
                    alarmManager.scheduleAt(decision.until, "(disabled until)")
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.toast_disabled_until, untilText),
                        Toast.LENGTH_LONG
                    ).show()
                    finishAndRemoveTask()
                }

                is LaunchDecision.Skip -> {
                    Log.i(LOGGING_TAG, "MainActivity - Out of operating hours or already started, finishing")
                    alarmManager.scheduleNextExecution()
                    finishAndRemoveTask()
                }
            }
        }
    }
}
