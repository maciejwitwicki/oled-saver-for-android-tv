package com.mwi.oledsaver

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.mwi.oledsaver.MainActivity.Companion.TAG
import kotlinx.coroutines.*
import java.net.URL
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.random.Random

class CatDisplayer : Activity() {

    private val IntervalInSeconds = 13 * 60
    private val DisplayLenghtInMillis = 1500L
    private val MinMarginDistance = 50
    private val CatServiceUrl = "https://cataas.com/cat"

    private lateinit var image: Bitmap

    init {
        val intervalMillis = (IntervalInSeconds * 1000).toLong();
        val delayMillis = intervalMillis - 2000;
        Timer().scheduleAtFixedRate(delayMillis, intervalMillis) {
            Thread {
                try {
                    Log.i(TAG, "Loading cat bitmap")
                    val url = URL(CatServiceUrl)
                    image = BitmapFactory.decodeStream(url.openStream())
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load bitmap", e)
                    e.printStackTrace()
                }
            }.start()
        }
    }

    fun mask(view: View) {
        Log.i(TAG, "Start Cat Displayer")
        val intervalMillis: Long = (IntervalInSeconds * 1000).toLong()
        Timer().scheduleAtFixedRate(intervalMillis, intervalMillis) {
            runOnUiThread{
                try {
                    Log.i(TAG, "Displaying cat bitmap")
                    displayRandomCatAtRandomPosition(view)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to mask", e)
                    e.printStackTrace()
                }
            }
        }
    }


    private fun displayRandomCatAtRandomPosition(view: View) {
        val layout = view.findViewById<FrameLayout>(R.id.randomCat)
        setupLayoutLocation(layout, view)

        val imageView = view.findViewById<ImageView>(R.id.randomCatImage)
        imageView.setImageBitmap(this.image)
        layout.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.Default).launch {
            delay(DisplayLenghtInMillis)
            runOnUiThread {
                layout.visibility = View.INVISIBLE
            }
        }
    }

    private fun setupLayoutLocation(layout: FrameLayout, view: View) {
        val container = view.findViewById<ConstraintLayout>(R.id.layoutContainer)
        val displayMetrics = container.resources.displayMetrics
        val containerWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
        val containerHeight = (displayMetrics.heightPixels / displayMetrics.density).toInt()
        val randomX = Random.nextInt(MinMarginDistance, containerWidth - MinMarginDistance)
        val randomY = Random.nextInt(MinMarginDistance, containerHeight - MinMarginDistance)

        val params = layout.layoutParams
        when (params) {
            is ViewGroup.MarginLayoutParams -> {
                params.marginEnd = randomX
                params.bottomMargin = randomY
            }
        }
        layout.layoutParams = params
    }
}