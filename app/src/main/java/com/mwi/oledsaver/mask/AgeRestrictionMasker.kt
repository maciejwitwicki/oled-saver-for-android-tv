package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper

class AgeRestrictionMasker(private val animationHelper: AnimationHelper) {

    fun mask(view: View, durationSeconds: Int) {
        Log.i(LOGGING_TAG, "Run Age restriction Masker")
        startBackgroundAnimator(view, durationSeconds)
        startRotationAnimator(view, durationSeconds)
        startTransparencyAnimator(view, durationSeconds)
    }

    private fun startTransparencyAnimator(view: View, durationSeconds: Int) {
        val component = view.findViewById<ShapeableImageView>(R.id.ageRestrictiondBackground)
        animationHelper.startTransparencyAnimator(component, durationSeconds)
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<ShapeableImageView>(R.id.ageRestrictiondBackground)
        animationHelper.startRotationAnimator(layout, durationSeconds)
    }

    private fun startBackgroundAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<ShapeableImageView>(R.id.ageRestrictiondBackground)
        animationHelper.startBackgroundAnimator(layout, durationSeconds);
    }

}
