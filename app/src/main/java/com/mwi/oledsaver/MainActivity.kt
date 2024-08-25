package com.mwi.oledsaver

import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.mwi.oledsaver.config.ConfigProvider


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        StrictMode.setVmPolicy(
            VmPolicy.Builder(StrictMode.getVmPolicy())
                .detectLeakedClosableObjects()
                .build()
        )

        if (CONFIG.isEnabled()) {
            Log.i(TAG, "Created MainActivity")
            setContentView(R.layout.activity_main)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            Log.i(TAG, "Out of operating hours, finishing")
            finishAndRemoveTask()
        }
    }

    companion object {
        val TAG: String = "oled-saver"
        val CONFIG: ConfigProvider = ConfigProvider()
    }

}