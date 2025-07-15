package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager

class LogoMasker(animationHelper: AnimationHelper, private val left: Float? = 32f) {

    private val animationSpeed = 150
    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(animationHelper)

    fun mask(view: View) {
        Log.i(LOGGING_TAG, "Run Logo Masker")
        startAnimator(view)
        if (left != null) {
            view.findViewById<ConstraintLayout>(R.id.logoMasker).x = left
        }
    }

    private fun startAnimator(view: View) {
        getImages(view).forEach { v -> animator.start(v, animationSpeed) }
    }

    private fun getImages(view: View): List<ShapeableImageView> {
        return listOf(R.id.bigLogoCircle, R.id.smallLogoCircle)
            .map { resource -> view.findViewById(resource) }
    }
}
