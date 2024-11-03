package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper

class ThinStripeMasker(private val animationHelper: AnimationHelper)  {

    fun mask(view: View, durationSeconds: Int) {
        Log.i(LOGGING_TAG, "Run Thin Stripe Masker")
        startBackgroundAnimator(view, durationSeconds)
        startRotationAnimator(view, durationSeconds)
        startTransparencyAnimator(view, durationSeconds)
    }

    private fun startTransparencyAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<View>(R.id.thinStripeBackground)
        animationHelper.startTransparencyAnimator(layout, durationSeconds)
    }


    private fun startBackgroundAnimator(view: View, durationSeconds: Int) {
        val imageView = view.findViewById<ShapeableImageView>(R.id.thinStripeBackground)
        animationHelper.startBackgroundAnimator(imageView, durationSeconds)
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<View>(R.id.thinStripeBackground)
        animationHelper.startRotationAnimator(layout, durationSeconds, 0.5f)
    }

}
