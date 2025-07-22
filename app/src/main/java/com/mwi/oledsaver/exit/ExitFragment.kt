package com.mwi.oledsaver.exit

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.alarm.AlarmDelay
import com.mwi.oledsaver.event.DismissMaskerEvent
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus


class ExitFragment : Fragment(R.layout.exit_fragment) {

    private val name = this.javaClass.simpleName

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(LOGGING_TAG, "$name onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.exit_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(LOGGING_TAG, "$name onViewCreated")

        view.findViewById<View>(R.id.exit_button_dismiss_5m)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_5_MIN)
            }
        view.findViewById<View>(R.id.exit_button_dismiss_10m)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_10_MIN)
            }
        view.findViewById<View>(R.id.exit_button_dismiss_1h)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_1_H)
            }
        view.findViewById<View>(R.id.exit_button_dismiss_1d)
            .setOnClickListener {
                dismiss(AlarmDelay.DELAY_NEXT_DAY)
            }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
                Log.i(LOGGING_TAG, "$name Back pressed!")
                dismiss(AlarmDelay.DELAY_5_MIN)
            }
    }

    private fun dismiss(delay: AlarmDelay) {
        lifecycleScope.launch {

        }
        EventBus.getDefault().post(DismissMaskerEvent(delay))
    }

}
