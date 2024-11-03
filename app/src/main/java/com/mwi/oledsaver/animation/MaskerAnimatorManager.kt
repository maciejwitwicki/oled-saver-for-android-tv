package com.mwi.oledsaver.animation

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.google.android.material.imageview.ShapeableImageView

class MaskerAnimatorManager(
    private val animationHelper: AnimationHelper,
    private val rotationDegree: Float = 360f
) {

    private var rotationAnimator: ValueAnimator? = null
    private var backgroundAnimator: ValueAnimator? = null
    private var transparencyAnimator: ObjectAnimator? = null
    private var heightAnimator: ValueAnimator? = null

    fun start(layout: View, component: ShapeableImageView, durationSeconds: Int) {
        if (isNotRunning(rotationAnimator))
            rotationAnimator =
                animationHelper.startRotationAnimator(component, durationSeconds, rotationDegree)

        if (isNotRunning(backgroundAnimator))
            backgroundAnimator = animationHelper.startBackgroundAnimator(component, durationSeconds)

        if (isNotRunning(transparencyAnimator))
            transparencyAnimator = animationHelper.startTransparencyAnimator(layout, durationSeconds)

        if (isNotRunning(heightAnimator))
            heightAnimator = animationHelper.startHeightAnimator(component, durationSeconds)
    }

    private fun isNotRunning(animator: Animator?): Boolean {
        return animator == null || !animator.isRunning
    }

}
