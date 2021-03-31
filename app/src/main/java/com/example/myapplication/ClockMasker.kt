package com.example.myapplication

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import java.time.Duration

class ClockMasker  {

    fun mask(view: View, durationSeconds: Int) {

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

        val layout = view.findViewById<FrameLayout>(R.id.clockMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(4f, 0f, 4f)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        val i: Long = (durationSeconds.toLong() / 2)
        rotationAnimator.duration = Duration.ofSeconds(i).toMillis()

        rotationAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            layout.rotation = progress
        }

        rotationAnimator.start()


    }
}