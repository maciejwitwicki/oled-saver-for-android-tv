package com.mwi.oledsaver.activity

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.FragmentActivity
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
