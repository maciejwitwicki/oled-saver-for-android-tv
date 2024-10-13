package com.mwi.oledsaver.activity

import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.fragment.app.FragmentActivity
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import com.mwi.oledsaver.R

class MaskerActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOGGING_TAG, "RestoreMaskerActivity - init")

        if (MASK_APP_CONFIG.isEnabled()) {
            setContentView(R.layout.activity_main)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            Log.i(LOGGING_TAG, "RestoreMaskerActivity - out of operating hours, quitting")
            finishAndRemoveTask()
        }
    }
}
