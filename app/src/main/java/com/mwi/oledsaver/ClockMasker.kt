package com.mwi.oledsaver

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.config.LayoutConfig

class ClockMasker(
    private val animationHelper: AnimationHelper,
    private val clockMaskerConfig: LayoutConfig.ClockMaskerLayoutSetup) {

    fun mask(view: View, durationSeconds: Int) {
        Log.i(MainActivity.TAG, "Run Clock Masker")
        setupLayoutLocation(view, clockMaskerConfig)
        startBackgroundAnimator(view, durationSeconds)
        startTransparencyAnimator(view, durationSeconds)
        if (clockMaskerConfig.rotation) {
            startRotationAnimator(view, durationSeconds)
        }
    }

    private fun startTransparencyAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<View>(R.id.clockMasker)
        animationHelper.startTransparencyAnimator(layout, durationSeconds)
    }

    private fun setupLayoutLocation(view: View, setup: LayoutConfig.ClockMaskerLayoutSetup) {
        // TODO: remove clock masker configurable layout if summer clock will remain unchanged in the TV show
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<View>(R.id.circleClockBackground)
        animationHelper.startRotationAnimator(layout, durationSeconds)
    }

    private fun startBackgroundAnimator(view: View, durationSeconds: Int) {
        val imageView = view.findViewById<ShapeableImageView>(R.id.circleClockBackground)
        animationHelper.startBackgroundAnimator(imageView, durationSeconds)
    }

}
