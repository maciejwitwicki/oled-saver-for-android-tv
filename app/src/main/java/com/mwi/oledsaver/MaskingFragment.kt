package com.mwi.oledsaver

import android.os.Bundle
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
    private val configProvider: ConfigProvider = ConfigProvider()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.masking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LogoMasker(configProvider.getLogoMaskerLayoutSetup()).mask(view, logoMaskerAnimationSpeed)
        ClockMasker(configProvider.getClockMaskerConfig()).mask(view, clockMaskerAnimationSpeed)
        BoldStripeMasker().mask(view, boldStripeAnimationSpeed)
        ThinStripeMasker().mask(view, thinStripAnimationSpeed)
        CatDisplayer().mask(view)
    }
}