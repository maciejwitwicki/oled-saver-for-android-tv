package com.mwi.oledsaver.activity

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.mwi.oledsaver.MainActivity.Companion.TAG
import com.mwi.oledsaver.R

class RestoreMaskerActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Created RestoreMaskerActivity")
        setContentView(R.layout.activity_main)
    }
}
