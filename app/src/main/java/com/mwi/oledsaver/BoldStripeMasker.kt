package com.mwi.oledsaver

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import java.time.Duration

class BoldStripeMasker  {

    private val spaceBetweenStripes = 250

    fun mask(view: View, durationSeconds: Int) {
        //Log.i(MainActivity.TAG, "Run Bold Stripe Masker")
        startBackgroundAnimation(view, durationSeconds)
        startRotationAnimation(view, durationSeconds)
        startHeightAnimator(view, durationSeconds)
    }

    private fun startRotationAnimation(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.boldStripeMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f, 0f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        val i: Long = (durationSeconds.toLong() / 2)
        rotationAnimator.duration = Duration.ofSeconds(i).toMillis()

        rotationAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            layout.rotation = -1 * progress
        }

        rotationAnimator.start()
    }

    private fun startBackgroundAnimation(view: View, durationSeconds: Int) {
        val image = view.findViewById<ImageView>(R.id.boldStripeBackground1)
        val image2 = view.findViewById<ImageView>(R.id.boldStripeBackground2)

        val animator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.RESTART
        animator.interpolator = LinearInterpolator()
        animator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = image.width.toFloat()
            val translation = width * progress
            var translation2 = (translation - width) - spaceBetweenStripes

            image.translationX = translation
            image2.translationX = translation2

        }
        animator.start()
    }

    private fun startHeightAnimator(view: View, durationSeconds: Int) {
        val animationSpeed = durationSeconds.toLong() / 4
        val layout = view.findViewById<FrameLayout>(R.id.boldStripeMasker)

        val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f, 1f, 1f)
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.interpolator = BounceInterpolator()
        animator.duration = Duration.ofSeconds(animationSpeed).toMillis()

        val maxHeight = layout.layoutParams.height
        val minHeight = maxHeight / 3
        val maxMovement = maxHeight - minHeight

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val translation = minHeight + maxMovement * progress
            val params = layout.layoutParams
            params.height = translation.toInt()
            layout.setLayoutParams(params)
        }

        animator.start()
    }
}