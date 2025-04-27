package com.mwi.oledsaver.animation

import com.google.android.material.imageview.ShapeableImageView

class MaskerAnimatorManager(
    private val animationHelper: AnimationHelper,
    private val rotationDegree: Float = 360f
) {

    fun start(component: ShapeableImageView, durationSeconds: Int) {
        animationHelper.startRotationAnimator(component, durationSeconds, rotationDegree)
        animationHelper.startBackgroundAnimator(component, durationSeconds)
    }

}
