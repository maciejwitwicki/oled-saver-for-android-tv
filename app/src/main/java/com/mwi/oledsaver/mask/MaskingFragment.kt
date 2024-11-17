package com.mwi.oledsaver.mask

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.MASK_APP_CONFIG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.exit.ExitActivity

class MaskingFragment : Fragment() {

    private val logoMaskerAnimationSpeed = 150
    private val clockMaskerAnimationSpeed = 140
    private val boldStripeAnimationSpeed = 60
    private val thinStripAnimationSpeed = 120
    private val ageRestrictionMaskerAnimationSpeed = 90

    private val animationHelper = AnimationHelper()

    private val boldStripeMasker = BoldStripeMasker(animationHelper)
    private val thinStripeMasker = ThinStripeMasker(animationHelper)
    private val clockMasker = ClockMasker(animationHelper)
    private val logoMasker = LogoMasker(animationHelper)
    private val ageRestrictionMasker = AgeRestrictionMasker(animationHelper)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(LOGGING_TAG, "MaskingFragment onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.masking_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val callback = requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner) {
                Log.i(LOGGING_TAG, "${this.javaClass.simpleName} Back pressed!")
                val intent = Intent(requireContext(), ExitActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            }
        //callback.isEnabled = true

        Log.i(LOGGING_TAG, "MaskingFragment onViewCreated")
        logoMasker.mask(view, logoMaskerAnimationSpeed)
        clockMasker.mask(view, clockMaskerAnimationSpeed)
        boldStripeMasker.mask(view, boldStripeAnimationSpeed)
        thinStripeMasker.mask(view, thinStripAnimationSpeed)
        ageRestrictionMasker.mask(view, ageRestrictionMaskerAnimationSpeed)
        CatDisplayer().mask(view)
    }
}
