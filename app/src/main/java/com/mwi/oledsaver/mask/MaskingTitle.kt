package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager

class MaskingTitle(animationHelper: AnimationHelper, private val text: String) {

    private val animationSpeed = 60
    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(
        animationHelper, rotationDegree = 0.5f
    )

    fun show(view: View) {
//        Log.i(LOGGING_TAG, "Run Bold Stripe Masker")
        startAnimator(view)

        val textView = view.findViewById<TextView>(R.id.maskingTitleTextView)
        textView.text = text

        val layout = view.findViewById<ConstraintLayout>(R.id.maskingTitle)
        animator.fadeOut(layout, animationSpeed * 100L)

    }

    private fun startAnimator(view: View) {
        val component = view.findViewById<ShapeableImageView>(R.id.boldStripeBackground)
        animator.start(component, animationSpeed)
    }

}
