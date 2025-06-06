package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.activity.MainActivity
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager
import com.mwi.oledsaver.config.LayoutConfig

class ClockMasker(
    private val animationHelper: AnimationHelper) {

    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(animationHelper)

    fun mask(view: View, durationSeconds: Int) {
        Log.i(LOGGING_TAG, "Run Clock Masker")
        startAnimator(view, durationSeconds)
    }

    private fun startAnimator(view: View, durationSeconds: Int) {
        val component = view.findViewById<ShapeableImageView>(R.id.circleClockBackground)
        animator.start(component, durationSeconds)
    }
}
