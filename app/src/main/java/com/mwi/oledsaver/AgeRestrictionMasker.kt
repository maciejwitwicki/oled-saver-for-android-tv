package com.mwi.oledsaver

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.mwi.oledsaver.config.LayoutConfig
import java.time.Duration

class AgeRestrictionMasker() {

    fun mask(view: View, durationSeconds: Int) {
        Log.i(MainActivity.TAG, "Run Age restriction Masker")
        startBackgroundAnimator(view, durationSeconds)
        startRotationAnimator(view, durationSeconds)
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.ageRestrictionMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 359.9f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.RESTART
        rotationAnimator.interpolator = AnticipateOvershootInterpolator()

        val i: Long = (durationSeconds.toLong() * 2)
        rotationAnimator.duration = Duration.ofSeconds(i).toMillis()

        rotationAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            layout.rotation = progress
        }

        rotationAnimator.start()
    }

    private fun startBackgroundAnimator(view: View, durationSeconds: Int) {
        val image = view.findViewById<ImageView>(R.id.ageRestrictiondBackground)
        val image2 = view.findViewById<ImageView>(R.id.ageRestrictionBackground2)

        val animator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.interpolator = LinearInterpolator()
        animator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = image.width.toFloat()
            val translation = width * progress
            val translation2 = (translation - width)

            image.translationX = translation
            image2.translationX = translation2
        }
        animator.start()
    }

}