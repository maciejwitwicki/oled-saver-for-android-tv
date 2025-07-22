package com.mwi.oledsaver.mask

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper

class MaskingFragmentTvp2 : Fragment(R.layout.masking_fragment_tvp2) {

    private val name = this.javaClass.simpleName

    private val animationHelper = AnimationHelper()

    private val maskingTitle = MaskingTitle(animationHelper, "TVP2")
    private val boldStripeMasker = BoldStripeMasker(animationHelper, 20, bottom = 20f, height = 100f)
    private val logoMaskerTvp2 = LogoMaskerTvp2(animationHelper, bottom = 36f, left = 50f)
    private val ageRestrictionMasker = AgeRestrictionMasker(animationHelper)

    // Using the activityViewModels() Kotlin property delegate from the
    // fragment-ktx artifact to retrieve the ViewModel in the activity scope.
    private val viewModel: ItemViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(LOGGING_TAG, "[$name] onCreateView")
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.masking_fragment_tvp2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(LOGGING_TAG, "[$name] onViewCreated")
        viewModel.changeMaskerVisibility(MaskerVisibilityRequest.AllVisible)
        startAllAnimations(view)

        viewModel.elementsState.observe(viewLifecycleOwner) { state ->
            Log.i(LOGGING_TAG, "[$name] received visibility state change $state")
            startAllAnimations(view)
            boldStripeMasker.setVisible(view, state.boldStripe)
        }
    }

    private fun startAllAnimations(view: View) {
        maskingTitle.show(view)
        logoMaskerTvp2.mask(view)
        boldStripeMasker.mask(view)
        ageRestrictionMasker.mask(view)
    }

}
