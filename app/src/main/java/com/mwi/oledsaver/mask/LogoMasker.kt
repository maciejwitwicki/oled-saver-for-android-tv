package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager

class LogoMasker(animationHelper: AnimationHelper) {

    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(animationHelper)

    fun mask(view: View, durationSeconds: Int) {
        Log.i(LOGGING_TAG, "Run Logo Masker")
        startAnimator(view, durationSeconds)
    }

    private fun startAnimator(view: View, durationSeconds: Int) {
        getImages(view).forEach { v -> animator.start(v, durationSeconds) }
    }

    private fun getImages(view: View): List<ShapeableImageView> {
        return listOf(R.id.bigLogoCircle, R.id.smallLogoCircle)
            .map { resource -> view.findViewById(resource) }
    }
}
