package com.mwi.oledsaver.activity

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
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

        OledSaverApplication.ALARM_MANAGER.initialize(applicationContext)

        val canDrawOverlays = Settings.canDrawOverlays(this)
        Log.i(LOGGING_TAG, "Can draw overlays $canDrawOverlays")

        if (!canDrawOverlays) {
            val askForPermission = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION)
            startActivity(askForPermission)
        }

        val settings = (application as OledSaverApplication).settingsRepository
        val intent = Intent(this, MaskerActivity::class.java)

        lifecycleScope.launch {
            settings.started.collect { started ->
                if (MASK_APP_CONFIG.isEnabled(started = started)) {
                    Log.i(LOGGING_TAG, "MainActivity - conditions met, starting MaskerActivity")
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    startActivity(intent)
                    settings.setStartedDate(Instant.now())
                    finish()
                } else {
                    Log.i(LOGGING_TAG, "MainActivity - Out of operating hours or already started, finishing")
                    finishAndRemoveTask()
                }
            }


        }
    }
}
