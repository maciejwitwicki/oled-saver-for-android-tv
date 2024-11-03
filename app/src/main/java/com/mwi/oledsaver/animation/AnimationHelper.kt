package com.mwi.oledsaver.animation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.graphics.Matrix
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AnimationSet
import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.R
import java.time.Duration
import kotlin.random.Random

class AnimationHelper {

    fun startRotationAnimator(layout: View, durationSeconds: Int, maxDegree: Float = 360f): ValueAnimator {
        val rotationAnimator: ValueAnimator = ValueAnimator.ofFloat(0f, maxDegree)
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
        return rotationAnimator;
    }

    fun startBackgroundAnimator(imageView: ShapeableImageView, durationSeconds: Int): ValueAnimator {
        val gradientAnimator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        gradientAnimator.repeatCount = ObjectAnimator.INFINITE
        gradientAnimator.repeatMode = ObjectAnimator.REVERSE
        gradientAnimator.interpolator = AccelerateDecelerateInterpolator()
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
        return gradientAnimator
    }

    fun startTransparencyAnimator(layout: View, durationSeconds: Int): ObjectAnimator? {
        val startingAlpha = layout.alpha
        val animator = ObjectAnimator.ofFloat(layout, View.ALPHA, startingAlpha, 0.6f, 0.2f)
        animator.startDelay = Random.nextLong(10_000)
        animator.repeatCount = INFINITE
        animator.repeatMode = REVERSE
        animator.interpolator = LinearInterpolator()
        animator.duration = Duration.ofSeconds(durationSeconds.toLong()).toMillis()

        animator.addUpdateListener { animation ->
            layout.alpha = animation.animatedFraction
        }

        animator.start()
        return animator
    }

    fun startHeightAnimator(layout: ShapeableImageView, durationSeconds: Int): ValueAnimator {
        val animationSpeed = durationSeconds.toLong()
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
        return animator;
    }
}
