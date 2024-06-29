package com.mwi.oledsaver

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Log.i(TAG, "Created MainActivity")
        setContentView(R.layout.activity_main)
    }

    companion object {
        val TAG: String = "oled-saver"
    }

}