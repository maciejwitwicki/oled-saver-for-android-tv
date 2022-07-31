package com.example.myapplication

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.TypedValue
import android.view.View
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import java.time.Duration

class LogoMasker  {

    //TODO: move setups to configuration
    val standardSetup = LayoutSetup(116, 120)
    val summerSetup = LayoutSetup(160, 170)

    fun mask(view: View, durationSeconds: Int) {
        setupLayoutLocation(view, summerSetup)
        startBackgroundAnimator(view, durationSeconds)
        startRotationAnimator(view, durationSeconds)
    }

    private fun setupLayoutLocation(view: View, setup: LayoutSetup) {
        val left = 60 - setup.width / 2
        val bottom = 50 - setup.height / 2
        val layout = view.findViewById<FrameLayout>(R.id.logoMasker)
        val params = layout.layoutParams
        val dm = layout.resources.displayMetrics
        val heightDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, setup.height.toFloat(), dm);
        val widthDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, setup.width.toFloat(), dm);
        params.width = widthDp.toInt()
        params.height = heightDp.toInt()
        layout.layoutParams = params
        layout.bottom = bottom
        layout.left = left
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.logoMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 15f, 0f)
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
        val image = view.findViewById<ImageView>(R.id.oledBackground)
        val image2 = view.findViewById<ImageView>(R.id.oledBackground2)

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

    data class LayoutSetup(val width: Int, val height: Int)
}