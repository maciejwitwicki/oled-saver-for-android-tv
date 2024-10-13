package com.mwi.oledsaver.animation

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.graphics.Matrix
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.BounceInterpolator
import com.google.android.material.imageview.ShapeableImageView
import java.time.Duration

class AnimationHelper {

    fun startRotationAnimator(layout: View, durationSeconds: Int) {
        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, 360f)
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

    fun startBackgroundAnimator(imageView: ShapeableImageView, durationSeconds: Int) {
        val gradientAnimator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        gradientAnimator.repeatCount = ObjectAnimator.INFINITE
        gradientAnimator.interpolator = BounceInterpolator()
        gradientAnimator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        val f = FloatArray(9)
        imageView.getImageMatrix().getValues(f)
        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        val scaleX = f[Matrix.MSCALE_X];
        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        val d = imageView.getDrawable();
        val origW = d.intrinsicWidth;
        // Calculate the actual dimensions
        val actualImageWidth = Math.round(origW * scaleX);
        val animationRange = actualImageWidth - imageView.width

        gradientAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val paddingStart = (progress * animationRange).toInt();

            val end = imageView.contentPaddingEnd
            val top = imageView.contentPaddingTop
            val bottom = imageView.contentPaddingBottom
            imageView.setContentPadding(paddingStart, top, end, bottom)
        }
        gradientAnimator.start()
    }

    fun startTransparencyAnimator(layout: View, durationSeconds: Int) {
       // val animator = ValueAnimator.ofFloat(0.8f, 1f)
        val animator = ObjectAnimator.ofFloat(layout, "alpha", 0.2f)
        animator.repeatCount = INFINITE
        animator.repeatMode = REVERSE
        animator.interpolator = AccelerateDecelerateInterpolator()
        animator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        animator.addUpdateListener { animation ->
            layout.alpha = animation.animatedFraction
        }

        animator.start()
    }
}
