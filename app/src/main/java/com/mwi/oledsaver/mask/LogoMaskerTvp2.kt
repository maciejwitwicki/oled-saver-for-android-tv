package com.mwi.oledsaver.mask

import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.R
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.animation.MaskerAnimatorManager


class LogoMaskerTvp2(
    animationHelper: AnimationHelper
) {

    private val animationSpeed = 150
    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(animationHelper, 3f)

    fun mask(view: View) {
        startAnimator(view)
    }

    private fun startAnimator(view: View) {
        getImages(view).forEach { v -> animator.start(v, animationSpeed) }
    }

    private fun getImages(view: View): List<ShapeableImageView> {
        return listOf(R.id.bigLogoCircleTvp2, R.id.smallLogoRectangleTvp2)
            .map { resource -> view.findViewById(resource) }
    }
}
