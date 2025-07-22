package com.mwi.oledsaver.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.mwi.oledsaver.OledSaverApplication
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.alarm.AlarmDelay
import com.mwi.oledsaver.alarm.MaskingAlarmManager
import com.mwi.oledsaver.event.DismissMaskerEvent
import com.mwi.oledsaver.mask.ItemViewModel
import com.mwi.oledsaver.mask.MaskerVisibilityRequest
import com.mwi.oledsaver.navigation.NavigationManager
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


class MaskerActivity : FragmentActivity(R.layout.activity_masker) {

    private val name = this.javaClass.simpleName

    // Using the viewModels() Kotlin property delegate from the activity-ktx
    // artifact to retrieve the ViewModel in the activity scope.
    private val viewModel: ItemViewModel by viewModels()
    private lateinit var navigationManager: NavigationManager
    private val maskingAlarmManager: MaskingAlarmManager = OledSaverApplication.ALARM_MANAGER

    private var exitFragmentDisplayed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val activityOptions = ActivityOptions.makeBasic()
        // will be required for android 34
        // activityOptions.pendingIntentBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED
        // activityOptions.pendingIntentCreatorBackgroundActivityStartMode = ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED

        intent.putExtras(activityOptions.toBundle())

        Log.i(LOGGING_TAG, "MaskerActivity - create")

        if (MASK_APP_CONFIG.isEnabled(true)) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

            val settings = (application as OledSaverApplication).settingsRepository

            lifecycleScope.launch {
                settings.maskIndex
                    .collect { index ->
                        navigationManager = NavigationManager(settings, index,navHostFragment.navController)
                    }

            }

        } else {
            Log.i(LOGGING_TAG, "MaskerActivity - out of operating hours, quitting")
            finishAndRemoveTask()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
//            KeyEvent.KEYCODE_DPAD_DOWN -> {
//                Log.i(LOGGING_TAG, "[$name] Down pressed!")
//                val elementState = viewModel.elementsState.value!!.invertBoldStripe()
//                viewModel.changeMaskerVisibility(elementState)
//                return true
//            }

            KeyEvent.KEYCODE_BACK -> {
                return handleBackPressed()
            }

            KeyEvent.KEYCODE_DPAD_LEFT -> {
                Log.i(LOGGING_TAG, "[$name] Left pressed!")
                lifecycleScope.launch {
                    navigationManager.navigateLeft()
                }
                return true
            }

            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                Log.i(LOGGING_TAG, "[$name] Right pressed!")
                lifecycleScope.launch {
                    navigationManager.navigateRight()
                }
                return true
            }
        }
        return false
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(event: DismissMaskerEvent) {
        Log.i(LOGGING_TAG, "[$name] DismissMaskerEvent received")
        handleDismissEvent(event)
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
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        Log.i(LOGGING_TAG, "MaskerActivity - stop")
        EventBus.getDefault().unregister(this)
    }

    override fun onActivityReenter(resultCode: Int, data: Intent?) {
        super.onActivityReenter(resultCode, data)
        Log.i(LOGGING_TAG, "MaskerActivity - activityReenter")
    }

    private fun handleBackPressed(): Boolean {
        Log.i(LOGGING_TAG, "[$name] Back pressed!")
        if (exitFragmentDisplayed) {
            handleDismissEvent(DismissMaskerEvent(AlarmDelay.DELAY_5_MIN))
        } else {
            maskingAlarmManager.cancelNextExecution()
            navigationManager.navigateToExit()
        }
        exitFragmentDisplayed = !exitFragmentDisplayed
        return true
    }

    private fun handleDismissEvent(event: DismissMaskerEvent) {
        exitFragmentDisplayed = false
        navigationManager.navigateToMasker()
        dismissMasker(event.delay)
        val settings = (application as OledSaverApplication).settingsRepository
        lifecycleScope.launch {
            settings.setStartedDate(null)
        }
    }

    private fun dismissMasker(delay: AlarmDelay) {
        Toast.makeText(this, "Odkładanie na ${delay.message}", Toast.LENGTH_SHORT).show()
        maskingAlarmManager.scheduleNextAlarmExecution(delay)
        finish()
    }
}
