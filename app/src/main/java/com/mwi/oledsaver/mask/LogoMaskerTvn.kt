package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager
import com.mwi.oledsaver.mask.LayoutUtils.getDpValue


class LogoMaskerTvn(animationHelper: AnimationHelper) {

    private val animationSpeed = 150
    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(animationHelper)

    fun mask(view: View) {
        Log.i(LOGGING_TAG, " Logo Masker")
        startAnimator(view)
    }

    private fun startAnimator(view: View) {
        getImages(view).forEach { v -> animator.start(v, animationSpeed) }
    }

    private fun getImages(view: View): List<ShapeableImageView> {
        return listOf(R.id.bigLogoCircleTvn, R.id.smallLogoCircleTvn)
            .map { resource -> view.findViewById(resource) }
    }
}
