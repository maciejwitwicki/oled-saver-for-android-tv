package com.mwi.oledsaver.mask

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager
import java.time.Duration

class BoldStripeMasker(animationHelper: AnimationHelper)  {

    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(
        animationHelper, rotationDegree = 0.5f)

    fun mask(view: View, durationSeconds: Int) {
        Log.i(LOGGING_TAG, "Run Bold Stripe Masker")
        startAnimator(view, durationSeconds)
    }

    private fun startAnimator(view: View, durationSeconds: Int) {
        val component = view.findViewById<ShapeableImageView>(R.id.boldStripeBackground)
        animator.start(view, component, durationSeconds)
    }
}
