package com.mwi.oledsaver.mask

import android.util.Log
import android.view.View
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.OledSaverApplication.OledSaverApplication.LOGGING_TAG
import com.mwi.oledsaver.R
import com.mwi.oledsaver.activity.MainActivity
import com.mwi.oledsaver.animation.AnimationHelper
import com.mwi.oledsaver.config.LayoutConfig

class LogoMasker(
    private val animationHelper: AnimationHelper,
    private val layoutConfig: LayoutConfig.LogoMaskerLayoutSetup
) {

    fun mask(view: View, durationSeconds: Int) {
        Log.i(LOGGING_TAG, "Run Logo Masker")
        setupLayoutLocation(view, layoutConfig)
        startBackgroundAnimator(view, durationSeconds)
        startRotationAnimator(view, durationSeconds)
        startTransparencyAnimator(view, durationSeconds)
    }

    private fun setupLayoutLocation(view: View, layoutConfig: LayoutConfig.LogoMaskerLayoutSetup) {
        // TODO: remove configurable layout if summer position will remain unchanged in the TV show
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        getImages(view).forEach { v -> animationHelper.startRotationAnimator(v, durationSeconds) }
    }

    private fun startTransparencyAnimator(view: View, durationSeconds: Int) {
        getImages(view).forEach { v ->
            animationHelper.startTransparencyAnimator(v, durationSeconds)
        }
    }

    private fun startBackgroundAnimator(view: View, durationSeconds: Int) {
        getImages(view).forEach { v -> animationHelper.startBackgroundAnimator(v, durationSeconds) }
    }

    private fun getImages(view: View): List<ShapeableImageView> {
        return listOf(getBiggerImageView(view), getSmallerImageView(view))
    }

    private fun getBiggerImageView(view: View): ShapeableImageView {
        return view.findViewById(R.id.bigLogoCircle)
    }

    private fun getSmallerImageView(view: View): ShapeableImageView {
        return view.findViewById(R.id.smallLogoCircle)
    }

}
