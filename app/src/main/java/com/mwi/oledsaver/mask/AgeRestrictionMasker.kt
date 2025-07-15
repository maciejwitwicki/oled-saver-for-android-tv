package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager

class AgeRestrictionMasker(animationHelper: AnimationHelper) {

    private val animationSpeed = 90
    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(animationHelper)

    fun mask(view: View) {
        Log.i(LOGGING_TAG, "Run Age restriction Masker")
        startAnimator(view)
    }

    private fun startAnimator(view: View) {
        val component = view.findViewById<ShapeableImageView>(R.id.ageRestrictiondBackground)
        animator.start(component, animationSpeed)
    }

}
