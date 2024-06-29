package com.mwi.oledsaver

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import com.mwi.oledsaver.config.LayoutConfig
import java.time.Duration

class ClockMasker(val clockMaskerConfig: LayoutConfig.ClockMaskerLayoutSetup) {

    fun mask(view: View, durationSeconds: Int) {
        //Log.i(MainActivity.TAG, "Run Clock Masker")
        setupLayoutLocation(view, clockMaskerConfig)
        startBackgroundAnimator(view, durationSeconds)
        if (clockMaskerConfig.rotation) {
            startRotationAnimator(view, durationSeconds)
        }
    }

    private fun setupLayoutLocation(view: View, setup: LayoutConfig.ClockMaskerLayoutSetup) {
        val layout = view.findViewById<FrameLayout>(R.id.clockMasker)
        val params = layout.layoutParams
        val dm = layout.resources.displayMetrics
        val widthDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, setup.width.toFloat(), dm);
        params.width = widthDp.toInt()

        when (params) {
            is ViewGroup.MarginLayoutParams -> {
                val rightDp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, setup.right.toFloat(), dm);
                params.marginEnd = rightDp.toInt()
            }
        }
        layout.layoutParams = params
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.clockMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(4f, 0f, 4f)
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
        val image = view.findViewById<ImageView>(R.id.oledBackground5)
        val image2 = view.findViewById<ImageView>(R.id.oledBackground6)

        val gradientAnimator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        gradientAnimator.repeatCount = ObjectAnimator.INFINITE
        gradientAnimator.interpolator = LinearInterpolator()
        gradientAnimator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        gradientAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val width = image.width.toFloat()
            val translation = width * progress
            var translation2 = (translation - width)

            image.translationX = translation
            image2.translationX = translation2

        }
        gradientAnimator.start()
    }


}