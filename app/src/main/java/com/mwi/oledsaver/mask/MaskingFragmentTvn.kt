package com.mwi.oledsaver.mask

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper


class MaskingFragmentTvn : Fragment(R.layout.masking_fragment_tvn) {

    private val name = this.javaClass.simpleName

    private val animationHelper = AnimationHelper()

    private val maskingTitle = MaskingTitle(animationHelper, "TVN")
    private val boldStripeMasker = BoldStripeMasker(animationHelper)
    private val thinStripeMasker = ThinStripeMasker(animationHelper)
    private val clockMasker = ClockMasker(animationHelper)
    private val logoMaskerTvn = LogoMaskerTvn(animationHelper)
    private val ageRestrictionMasker = AgeRestrictionMasker(animationHelper)

    // Using the activityViewModels() Kotlin property delegate from the
    // fragment-ktx artifact to retrieve the ViewModel in the activity scope.
    private val viewModel: ItemViewModel by activityViewModels()

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        Log.i(LOGGING_TAG, "$name onCreateView")
//        // Inflate the layout for this fragment
//        //return inflater.inflate(R.layout.masking_fragment_tvn_old2relat, container, false)
//        return null
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(LOGGING_TAG, "$name onViewCreated")
        startAllAnimations(view)

        viewModel.elementsState.observe(viewLifecycleOwner) { state ->
            Log.i(LOGGING_TAG, "$name received visibility state change $state")
            boldStripeMasker.setVisible(view, state.boldStripe)
        }

    }

    override fun onResume() {
        super.onResume()

        Log.i(LOGGING_TAG, "$name onResume")


        // Disable back press
        requireView().setFocusableInTouchMode(true)
        requireView().requestFocus()
        requireView().setOnKeyListener(onKeyListener)

        startAllAnimations(requireView())

    }

    private fun startAllAnimations(view: View) {
        maskingTitle.show(view)
        logoMaskerTvn.mask(view)
        clockMasker.mask(view)
        boldStripeMasker.mask(view)
        thinStripeMasker.mask(view)
        ageRestrictionMasker.mask(view)
    }

    private val onKeyListener =
        { v: View, keyCode: Int, event: KeyEvent ->

            Log.i(LOGGING_TAG, "[$name] onKeyListener!")


            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    Log.i(LOGGING_TAG, "[$name] Down pressed!")
                    val elementState = viewModel.elementsState.value!!.invertBoldStripe()
                    viewModel.changeMaskerVisibility(elementState)

                }
            }
            false
        }

}
