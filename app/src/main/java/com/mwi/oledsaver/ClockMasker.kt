package com.mwi.oledsaver

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Matrix
import android.util.Log
import android.view.View
import android.view.animation.BounceInterpolator
import android.widget.FrameLayout
import com.google.android.material.imageview.ShapeableImageView
import com.mwi.oledsaver.config.LayoutConfig
import java.time.Duration

class ClockMasker(val clockMaskerConfig: LayoutConfig.ClockMaskerLayoutSetup) {

    fun mask(view: View, durationSeconds: Int) {
        Log.i(MainActivity.TAG, "Run Clock Masker")
        setupLayoutLocation(view, clockMaskerConfig)
        startBackgroundAnimator(view, durationSeconds)
        if (clockMaskerConfig.rotation) {
            startRotationAnimator(view, durationSeconds)
        }
    }

    private fun setupLayoutLocation(view: View, setup: LayoutConfig.ClockMaskerLayoutSetup) {
        // TODO: remove clock masker configurable layout if summer clock will remain unchanged in the TV show
    }

    private fun startRotationAnimator(view: View, durationSeconds: Int) {
        val layout = view.findViewById<FrameLayout>(R.id.clockMasker)

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

    private fun startBackgroundAnimator(view: View, durationSeconds: Int) {
        val imageView = view.findViewById<ShapeableImageView>(R.id.circleClockBackground)

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


}
