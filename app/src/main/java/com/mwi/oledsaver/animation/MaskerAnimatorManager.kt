package com.mwi.oledsaver.animation

import android.view.View
import com.google.android.material.imageview.ShapeableImageView

class MaskerAnimatorManager(
    private val animationHelper: AnimationHelper,
    private val rotationDegree: Float = 360f
) {

    fun start(component: ShapeableImageView, durationSeconds: Int) {
        animationHelper.startRotationAnimator(component, durationSeconds, rotationDegree)
        animationHelper.startBackgroundAnimator(component, durationSeconds)
    }

    fun fadeOut(component: View, durationMillis: Long) {
        animationHelper.fadeOut(component, durationMillis)
            .start()
    }

}
