package com.mwi.oledsaver

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import java.time.Duration

class ThinStripeMasker  {

    private val SpaceBetweenStripes = 150

    fun mask(view: View, durationSeconds: Int) {
        Log.i(MainActivity.TAG, "Run Thin Stripe Masker")
        startBackgroundAnimator(view, durationSeconds)
        startRotationAnimator(view, durationSeconds)
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.thinStripeMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f, 0f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.REVERSE
        rotationAnimator.interpolator = BounceInterpolator()
        val i: Long = (durationSeconds.toLong() / 2)
        rotationAnimator.duration = Duration.ofSeconds(i).toMillis()

        rotationAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            layout.rotation = progress
        }

        rotationAnimator.start()
    }

    private fun startBackgroundAnimator(view: View, durationSeconds: Int) {
        val image = view.findViewById<ImageView>(R.id.thinStripeBackground1)
        val image2 = view.findViewById<ImageView>(R.id.thinStripeBackground2)

        val animator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.interpolator = BounceInterpolator()
        animator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = image.width.toFloat()
            val translation = width * progress
            var translation2 = (translation - width) - SpaceBetweenStripes

            image.translationX = translation
            image2.translationX = translation2

        }
        animator.start()
    }
}