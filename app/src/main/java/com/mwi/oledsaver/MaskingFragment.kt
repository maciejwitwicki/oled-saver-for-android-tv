package com.mwi.oledsaver

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mwi.oledsaver.config.ConfigProvider

class MaskingFragment : Fragment() {

    private val logoMaskerAnimationSpeed = 150
    private val clockMaskerAnimationSpeed = 140
    private val boldStripeAnimationSpeed = 60
    private val thinStripAnimationSpeed = 120
    private val ageRestrictionMaskerAnimationSpeed = 150
    private val config = MainActivity.CONFIG


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(MainActivity.TAG, "MaskingFragment onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.masking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i(MainActivity.TAG, "MaskingFragment onViewCreated")
        LogoMasker(config.getLogoMaskerLayoutSetup()).mask(view, logoMaskerAnimationSpeed)
        ClockMasker(config.getClockMaskerConfig()).mask(view, clockMaskerAnimationSpeed)
        BoldStripeMasker().mask(view, boldStripeAnimationSpeed)
        ThinStripeMasker().mask(view, thinStripAnimationSpeed)
        AgeRestrictionMasker().mask(view, ageRestrictionMaskerAnimationSpeed)
        CatDisplayer().mask(view)
    }
}