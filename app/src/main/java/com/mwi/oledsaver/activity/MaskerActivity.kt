package com.mwi.oledsaver.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.exit.ExitActivity
import com.mwi.oledsaver.mask.ItemViewModel
import com.mwi.oledsaver.mask.MaskerVisibilityRequest


class MaskerActivity : FragmentActivity() {
    // Using the viewModels() Kotlin property delegate from the activity-ktx
    // artifact to retrieve the ViewModel in the activity scope.
    private val viewModel: ItemViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityOptions = ActivityOptions.makeBasic()
        // will be required for android 34
        // activityOptions.pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
        //activityOptions.pendingIntentCreatorBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED

        intent.putExtras(activityOptions.toBundle())

        Log.i(LOGGING_TAG, "MaskerActivity - create")

        if (MASK_APP_CONFIG.isEnabled(true)) {
            setContentView(R.layout.activity_main)
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            Log.i(LOGGING_TAG, "MaskerActivity - out of operating hours, quitting")
            finishAndRemoveTask()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                val elementState = viewModel.elementsState.value!!.invertBoldStripe()
                viewModel.changeMaskerVisibility(elementState)
                return true
            }
            KeyEvent.KEYCODE_BACK -> {
                Log.i(LOGGING_TAG, "${this.javaClass.simpleName} Back pressed!")
                val intent = Intent(this , ExitActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return false
    }

    override fun onRestart() {
        super.onRestart()
        Log.i(LOGGING_TAG, "MaskerActivity - restart")
    }

    override fun onResume() {
        super.onResume()
        Log.i(LOGGING_TAG, "MaskerActivity - resume")
        viewModel.changeMaskerVisibility(MaskerVisibilityRequest.AllVisible)
    }

    override fun onPause() {
        super.onPause()
        Log.i(LOGGING_TAG, "MaskerActivity - pause")

    }

    override fun onStart() {
        super.onStart()
        Log.i(LOGGING_TAG, "MaskerActivity - start")
    }

    override fun onStop() {
        super.onStop()
        Log.i(LOGGING_TAG, "MaskerActivity - stop")
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        Log.i(LOGGING_TAG, "MaskerActivity - activityReenter")

    }
}
