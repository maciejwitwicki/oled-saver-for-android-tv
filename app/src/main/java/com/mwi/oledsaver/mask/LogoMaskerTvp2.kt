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


class LogoMaskerTvp2(
    animationHelper: AnimationHelper,
    private val bottom: Float = 10f,
    private val left: Float = 32f
) {

    private val animationSpeed = 150
    private val animator: MaskerAnimatorManager = MaskerAnimatorManager(animationHelper, 3f)

    fun mask(view: View) {
//        Log.i(LOGGING_TAG, "Run Logo Masker")
        startAnimator(view)
        val logoMasker = view.findViewById<ConstraintLayout>(R.id.logoMaskerTvp2)
        val params = logoMasker.layoutParams
        val updatedParams = ConstraintLayout.LayoutParams(params)

        updatedParams.setMargins(
            getDpValue(left, view),
            updatedParams.topMargin,
            updatedParams.rightMargin,
            getDpValue(bottom, view)
        )
        logoMasker.layoutParams = updatedParams

    }

    private fun startAnimator(view: View) {
        getImages(view).forEach { v -> animator.start(v, animationSpeed) }
    }

    private fun getImages(view: View): List<ShapeableImageView> {
        return listOf(R.id.bigLogoCircleTvp2, R.id.smallLogoRectangleTvp2)
            .map { resource -> view.findViewById(resource) }
    }
}
