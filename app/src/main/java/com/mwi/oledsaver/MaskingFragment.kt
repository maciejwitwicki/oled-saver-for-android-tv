package com.mwi.oledsaver

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mwi.oledsaver.config.ConfigProvider

class MaskingFragment : Fragment() {

    private var configProvider: ConfigProvider = ConfigProvider()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.masking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        LogoMasker(configProvider.getLogoMaskerLayoutSetup()).mask(view, 150)
        ThinStripeMasker().mask(view, 120)
        ClockMasker(configProvider.getClockMaskerConfig()).mask(view, 140)
        BoldStripeMasker().mask(view, 100)
    }
}