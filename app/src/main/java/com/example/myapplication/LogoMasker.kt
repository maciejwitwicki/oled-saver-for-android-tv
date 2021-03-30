package com.example.myapplication

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import java.time.Duration

class LogoMasker  {

    fun mask(view: View, durationSeconds: Int) {

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


        val layout = view.findViewById<FrameLayout>(R.id.logoMasker)

        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 15f, 0f)
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