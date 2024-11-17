package com.mwi.oledsaver.animation

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.animation.ValueAnimator.INFINITE
import android.animation.ValueAnimator.REVERSE
import android.graphics.Matrix
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import com.mwi.oledsaver.R

import android.view.animation.BounceInterpolator
import android.view.animation.LinearInterpolator
import com.google.android.material.imageview.ShapeableImageView
import java.time.Duration
import kotlin.random.Random

class AnimationHelper {

    private fun Animator.onEnd(fn: () -> Unit) {
        this.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(a: Animator) {
                fn.invoke()
            }
        })
    }

    private fun Animator.onStart(fn: () -> Unit) {
        this.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(a: Animator) {
                fn.invoke()
            }
        })
    }

    fun startRotationAnimator(
        layout: View,
        durationSeconds: Int,
        maxDegree: Float = 360f
    ): ValueAnimator {
        val rotationAnimator = ObjectAnimator.ofFloat(layout, View.ROTATION, maxDegree)
        rotationAnimator.repeatCount = ObjectAnimator.INFINITE
        rotationAnimator.repeatMode = ObjectAnimator.REVERSE
        rotationAnimator.startDelay = Random.nextLong(10_000)
        rotationAnimator.interpolator = BounceInterpolator()
        val i: Long = (durationSeconds.toLong() / 2)
        rotationAnimator.duration = Duration.ofSeconds(i).toMillis()

        rotationAnimator.start()
        return rotationAnimator;
    }

    fun startBackgroundAnimator(imageView: ShapeableImageView, durationSeconds: Int) {
        val durationMillis = Duration.ofSeconds(durationSeconds.toLong()).toMillis()
        val gradientAnimation = createGradientAnimator(imageView, durationMillis)
        val blackBackgroundAnimation = createBlackBackgroundAnimator(imageView, durationMillis)

        val animation = AnimatorSet()
        animation.playSequentially(
            gradientAnimation,
            blackBackgroundAnimation
        )
        animation.onEnd { animation.start() }
        animation.start()
    }

    private fun createBlackBackgroundAnimator(
        imageView: ShapeableImageView,
        durationMillis: Long
    ): AnimatorSet {
        val blackBackgroundAnimator =
            ObjectAnimator.ofFloat(imageView, View.ALPHA, 1F, .95f, .9f, .8F, .7F, .5F)
        blackBackgroundAnimator.repeatCount = 5
        blackBackgroundAnimator.repeatMode = REVERSE
        blackBackgroundAnimator.interpolator = AccelerateInterpolator(2f)
        blackBackgroundAnimator.duration = durationMillis
        val blackAnim = AnimatorSet()
        blackAnim.play(blackBackgroundAnimator)
            .after(fadeIn(imageView, durationMillis / 4))
        blackAnim.onStart { imageView.setImageResource(R.color.black) }
        return blackAnim
    }

    private fun createGradientAnimator(
        imageView: ShapeableImageView,
        durationMillis: Long
    ): AnimatorSet {

        val gradientTranslateAnimator: ValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f)
        gradientTranslateAnimator.repeatCount = 3
        gradientTranslateAnimator.repeatMode = REVERSE
        gradientTranslateAnimator.interpolator = LinearInterpolator()

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

        gradientTranslateAnimator.addUpdateListener { animation ->
            val progress = animation.animatedValue as Float
            val paddingStart = (progress * animationRange).toInt();

            val end = imageView.contentPaddingEnd
            val top = imageView.contentPaddingTop
            val bottom = imageView.contentPaddingBottom
            imageView.setContentPadding(paddingStart, top, end, bottom)

        }
        gradientTranslateAnimator.onStart {
            imageView.setImageResource(R.drawable.oled)
        }

        val transparencyAnimator = ObjectAnimator.ofFloat(imageView, View.ALPHA, 1f, 0.5f)
        transparencyAnimator.repeatCount  = 1
        transparencyAnimator.repeatMode = REVERSE

        val gradientAnimator = AnimatorSet()
        gradientAnimator.duration = durationMillis
        gradientAnimator.play(gradientTranslateAnimator)
            .with(transparencyAnimator)

        val gradientFadeIn = fadeIn(imageView, durationMillis / 4)
        val gradientFadeOut = fadeOut(imageView, durationMillis / 4)

        val gradientAnimation = AnimatorSet()
        gradientAnimation
            .play(gradientAnimator)
            .before(gradientFadeOut)
            .after(gradientFadeIn)
        return gradientAnimation
    }

    private fun fadeIn(target: View, durationMillis: Long): ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(target, View.ALPHA, 0f, 1F)
        anim.duration = durationMillis
        return anim
    }

    private fun fadeOut(target: View, durationMillis: Long): ObjectAnimator {
        val anim = ObjectAnimator.ofFloat(target, View.ALPHA, 1f, 0f)
        anim.duration = durationMillis
        return anim
    }
}
