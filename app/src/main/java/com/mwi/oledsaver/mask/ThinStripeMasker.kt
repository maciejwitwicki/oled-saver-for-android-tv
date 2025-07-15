package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager

class ThinStripeMasker(animationHelper: AnimationHelper) {

    private val animationSpeed = 120
    private val animator: MaskerAnimatorManager =
        MaskerAnimatorManager(animationHelper, rotationDegree = 0.5f)

    fun mask(view: View) {
        Log.i(LOGGING_TAG, "Run Thin Stripe Masker")
        startAnimator(view)
    }

    private fun startAnimator(view: View) {
        val component = view.findViewById<ShapeableImageView>(R.id.thinStripeBackground)
        animator.start(component, animationSpeed)
    }
}
