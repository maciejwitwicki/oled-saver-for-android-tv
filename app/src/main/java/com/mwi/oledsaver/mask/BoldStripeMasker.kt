package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager
import com.mwi.oledsaver.mask.LayoutUtils.getDpValue

class BoldStripeMasker(
    animationHelper: AnimationHelper,
    private val marginRight: Int = 130,
    private val bottom: Float = 58f,
    private val height: Float = 90f
) {

    private val animationSpeed = 60
    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(
        animationHelper, rotationDegree = 0.5f
    )

    fun mask(view: View) {
//        Log.i(LOGGING_TAG, "Run Bold Stripe Masker")
        startAnimator(view)

        val layout = view.findViewById<ConstraintLayout>(R.id.boldStripeMasker)
        val params = layout.layoutParams
        when (params) {
            is ViewGroup.MarginLayoutParams -> {
                params.marginEnd = marginRight
                params.bottomMargin = getDpValue(bottom, layout)
                params.height = getDpValue(height, layout)
            }
        }
        layout.layoutParams = params

    }

    private fun startAnimator(view: View) {
        val component = view.findViewById<ShapeableImageView>(R.id.boldStripeBackground)
        animator.start(component, animationSpeed)
    }

    fun setVisible(view: View, boldStripe: Boolean) {
        val component = view.findViewById<ShapeableImageView>(R.id.boldStripeBackground)
        if (boldStripe) {
            component.visibility = View.VISIBLE
        } else component.visibility = View.INVISIBLE
    }
}
