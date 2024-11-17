package com.mwi.oledsaver.exit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R

class ExitActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(LOGGING_TAG, "${this.javaClass.simpleName} - create")
        setContentView(R.layout.activity_exit)


        findViewById<View>(R.id.exit_button_dismiss_5m)
            .setOnClickListener {
                dismiss("5m")
            }
        findViewById<View>(R.id.exit_button_dismiss_10m)
            .setOnClickListener {
                dismiss("10m")
            }
        findViewById<View>(R.id.exit_button_dismiss_1d)
            .setOnClickListener {
                dismiss("1d")
            }
    }


    private fun dismiss(text: String) {
        Toast.makeText(this, "Odkladanie na ${text}", Toast.LENGTH_SHORT).show()
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
