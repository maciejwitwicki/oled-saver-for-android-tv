package com.example.myapplication

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import java.time.Duration

class BoldStripeMasker  {

    private val SpaceBetweenStripes = 250

    fun mask(view: View, durationSeconds: Int) {
        startBackgroundAnimation(view, durationSeconds)
        startRotationAnimation(view, durationSeconds)
        startLocationAnimator(view, durationSeconds)
    }

    private fun startRotationAnimation(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.boldStripeMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f, 0f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        val i: Long = (durationSeconds.toLong() / 2)
        rotationAnimator.duration = Duration.ofSeconds(i).toMillis()

        rotationAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            layout.rotation = progress
        }

        rotationAnimator.start()
    }

    private fun startBackgroundAnimation(view: View, durationSeconds: Int) {
        val image = view.findViewById<ImageView>(R.id.boldStripeBackground1)
        val image2 = view.findViewById<ImageView>(R.id.boldStripeBackground2)

        val animator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.interpolator = LinearInterpolator()
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

    private fun startLocationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.boldStripeMasker)

        val animator: ValueAnimator = ValueAnimator.ofFloat(0f, 1f)
        animator.repeatCount = ObjectAnimator.INFINITE
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        val maxMovement = 300

        animator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val translation = maxMovement * progress
            val params = layout.layoutParams
            params.height = translation.toInt()
            layout.setLayoutParams(params)
        }

        animator.start()
    }
}